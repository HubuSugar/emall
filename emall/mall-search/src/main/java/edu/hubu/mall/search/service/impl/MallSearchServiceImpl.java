package edu.hubu.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import edu.hubu.mall.common.to.es.SkuEsModel;
import edu.hubu.mall.search.constant.ElasticConstant;
import edu.hubu.mall.search.service.MallSearchService;
import edu.hubu.mall.search.vo.SearchParamVo;
import edu.hubu.mall.search.vo.SearchResultVo;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.apache.lucene.util.CollectionUtil;
import org.apache.lucene.util.QueryBuilder;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.MultiBucketsAggregation;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static edu.hubu.mall.search.config.MallElasticConfig.COMMON_OPTIONS;

/**
 * @Author: huxiaoge
 * @Date: 2021-05-12
 * @Description: 搜索服务
 **/
@Service
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 根据参数查询商品搜索结果
     * @param param
     * @return
     */
    @Override
    public SearchResultVo skuSearch(SearchParamVo param) {
        SearchRequest searchRequest =  buildSearchRequest(param);
        SearchResultVo searchResult = null;
        try {
            SearchResponse search = restHighLevelClient.search(searchRequest, COMMON_OPTIONS);
            searchResult =  buildSearchResult(search,param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return searchResult;
    }

    /**
     * 构建检索请求
     */
    private SearchRequest buildSearchRequest(SearchParamVo param){

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //1.构建Query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //1.1 mustQuery  根据skuTitle匹配
        if(StringUtils.isNotEmpty(param.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
        }
        //1.2 filterQuery 根据catalog3Id
        if(null != param.getCatalog3Id()){
            boolQuery.filter(QueryBuilders.termQuery("catalogId",param.getCatalog3Id()));
        }

        //1.3 filterQuery 根据多个品牌id查询
        if(!CollectionUtils.isEmpty(param.getBrands())){
            boolQuery.filter(QueryBuilders.termsQuery("brandId",param.getBrands()));
        }

        // 1.4 filterQuery 根据属性attrs查询 nestQuery  格式attrs=1_ios:android
        if(!CollectionUtils.isEmpty(param.getAttrs())){
            for(String attrStr: param.getAttrs()){

                BoolQueryBuilder nestedboolQuery = QueryBuilders.boolQuery();
                String[] attrArr = attrStr.split("_");
                String attrId = attrArr[0];
                String[] attrVals = attrArr[1].split(":");
                nestedboolQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestedboolQuery.must(QueryBuilders.termsQuery("attrs.attrValue",attrVals));

                //每一个属性都得生成一个nested查询
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedboolQuery, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }

        }

        //1.5 filterQuery 根据是否有存库
        boolQuery.filter(QueryBuilders.termQuery("hasStock",param.getHasStock() == 1));

        //1.6 filterQuery 价格区间
        if(StringUtils.isNotEmpty(param.getSkuPrice())){
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String skuPrice = param.getSkuPrice();
            String[] priceRange = skuPrice.split("_");
            if(skuPrice.startsWith("_")){
                rangeQuery.lte(priceRange[1]);
            }else if(skuPrice.endsWith("_")){
                rangeQuery.gte(priceRange[0]);
            }else{   //最高价格和最低价格都存在
                rangeQuery.gte(priceRange[0]).lte(priceRange[1]);
            }
            boolQuery.filter(rangeQuery);
        }

        //封装查询条件
        sourceBuilder.query(boolQuery);

        //开始构建排序，分页，高亮
        //2.1 排序
        if(StringUtils.isNotEmpty(param.getSort())){
            String sortStr = param.getSort();
            String[] sortArr = sortStr.split("_");
            String sortField = sortArr[0];   //排序字段
            SortOrder sortOrder  = sortArr[1].equalsIgnoreCase("asc" ) ? SortOrder.ASC : SortOrder.DESC;  //升降序
            sourceBuilder.sort(sortField,sortOrder);
        }

        //2.2 分页
        Integer pageNum = param.getPageNum();
        int from = (pageNum - 1) * ElasticConstant.PRODUCT_ES_PAGESIZE;
        sourceBuilder.from(from);
        sourceBuilder.size(ElasticConstant.PRODUCT_ES_PAGESIZE);

        //2.3 高亮
        if(StringUtils.isNotEmpty(param.getKeyword())){
            HighlightBuilder builder = new HighlightBuilder();
            builder.field("skuTitle");
            builder.preTags("<b style='color:red'>");
            builder.postTags("</b>");
            sourceBuilder.highlighter(builder);
        }

        /**
         * 3.聚合分析
         */
        //3.1品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);
        //品牌聚合的子聚合,品牌名称和品牌图片
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));

        sourceBuilder.aggregation(brand_agg);

        //3.2分类聚合
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
        catalog_agg.field("catalogId").size(1);
        //分类的子聚合，分类名称
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));

        sourceBuilder.aggregation(catalog_agg);

        //3.3属性聚合,nest嵌入式聚合
        NestedAggregationBuilder nested = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg");
        attr_id_agg.field("attrs.attrId").size(10);
        //属性子聚合 --属性名、属性值
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(10));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        nested.subAggregation(attr_id_agg);
        sourceBuilder.aggregation(nested);

        return new SearchRequest(new String[]{ElasticConstant.PRODUCT_ES_INDEX},sourceBuilder);
    }

    /**
     * 处理返回结果
     * @param response
     * @return
     */
    private SearchResultVo buildSearchResult(SearchResponse response,SearchParamVo searchParam) {

        SearchResultVo result = new SearchResultVo();
        //查询到数据信息
        SearchHits hits = response.getHits();
        long total = hits.getTotalHits();//总记录数
        result.setTotal(total);
        //总页数
        int totalPages = (int)total % ElasticConstant.PRODUCT_ES_PAGESIZE == 0 ?
                (int) total / ElasticConstant.PRODUCT_ES_PAGESIZE : (int)total / ElasticConstant.PRODUCT_ES_PAGESIZE + 1;
        result.setTotalPages(totalPages);
        //当前页
        result.setPageNum(searchParam.getPageNum());

        List<SkuEsModel> esSkus = new ArrayList<>();
        //查询到的记录的数据信息
        if(hits.getHits() != null && hits.getHits().length > 0){
            for (SearchHit hit:hits.getHits()) {
                String sourceStr = hit.getSourceAsString();
                SkuEsModel esModel = JSON.parseObject(sourceStr, SkuEsModel.class);
                //高亮显示
                if(StringUtils.isNotEmpty(searchParam.getKeyword())){
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String highlightTitle = skuTitle.fragments()[0].string();
                    esModel.setSkuTitle(highlightTitle);
                }
                esSkus.add(esModel);
            }
        }
        result.setProducts(esSkus);

        //查询到的聚合信息
        Aggregations aggregations = response.getAggregations();

        //分类聚合信息
        ParsedLongTerms catalog_agg = aggregations.get("catalog_agg");
        List<SearchResultVo.CatalogVo> catalogs = new ArrayList<>();
        List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
        for(Terms.Bucket bucket:buckets){
            SearchResultVo.CatalogVo catalogVo = new SearchResultVo.CatalogVo();
            catalogVo.setCatalogId(Long.parseLong(bucket.getKeyAsString()));  //得到的分类id

            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            String catalogName = catalog_name_agg.getBuckets().get(0).getKeyAsString();

            catalogVo.setCatalogName(catalogName);
            catalogs.add(catalogVo);
        }
        result.setCatalogs(catalogs);

        //品牌聚合信息
        ParsedLongTerms brand_agg = aggregations.get("brand_agg");
        List<? extends Terms.Bucket> brandBuckets = brand_agg.getBuckets();
        List<SearchResultVo.BrandVo> brands = new ArrayList<>();
        for(Terms.Bucket bucket:brandBuckets){
            SearchResultVo.BrandVo brandVo = new SearchResultVo.BrandVo();
            brandVo.setBrandId(Long.parseLong(bucket.getKeyAsString()));   //品牌Id
            //品牌的名字
            ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
            String brandName = brand_name_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);
            //品牌的图标
            ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
            String brandImg = brand_img_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);
            brands.add(brandVo);
        }
        result.setBrands(brands);

        //属性的聚合
        ParsedNested attrs_agg = aggregations.get("attr_agg");
        ParsedLongTerms attr_id_agg = attrs_agg.getAggregations().get("attr_id_agg");
        List<SearchResultVo.AttrVo> attrs = new ArrayList<>();
        List<? extends Terms.Bucket> attrBucket = attr_id_agg.getBuckets();
        for(Terms.Bucket bucket:attrBucket){
            SearchResultVo.AttrVo attrVo = new SearchResultVo.AttrVo();
            attrVo.setAttrId(Long.parseLong(bucket.getKeyAsString()));
            //属性名
            ParsedStringTerms attr_name_agg = bucket.getAggregations().get("attr_name_agg");
            String attrName = attr_name_agg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);
            //属性值
            ParsedStringTerms attr_value_agg = bucket.getAggregations().get("attr_name_agg");
            List<? extends Terms.Bucket> attrValue = attr_value_agg.getBuckets();
            List<String> attrValues = attrValue.stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
            attrVo.setAttrValues(attrValues);

            attrs.add(attrVo);
        }
        result.setAttrs(attrs);

        /**
         * 添加可分页的页码信息
         */
        List<Integer> pages = new ArrayList<>();
        for(int i =1; i <= totalPages;i++){
            pages.add(i);
        }
        result.setPageNavs(pages);

        /**
         * 添加面包屑的导航功能
         */
        List<SearchResultVo.BreadVo> breads;
        if(!CollectionUtils.isEmpty(searchParam.getAttrs())){
//            searchParam.getAttrs().stream().map()


            breads = searchParam.getAttrs().stream().map(attr -> {
                SearchResultVo.BreadVo breadVo = new SearchResultVo.BreadVo();
                String[] s = attr.split("_");
                //属性的值
                breadVo.setBreadValue(s[1]);


//                breadVo.setBreadName();

                return breadVo;
            }).collect(Collectors.toList());

        }





//        result.setBreads(breads);

        return result;
    }


}
