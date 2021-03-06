package edu.hubu.mall.search.service.impl;

import com.alibaba.fastjson.JSON;
import edu.hubu.mall.common.es.SkuEsModel;
import edu.hubu.mall.search.constant.ElasticConstant;
import edu.hubu.mall.search.entity.AttrEntity;
import edu.hubu.mall.search.entity.BrandEntity;
import edu.hubu.mall.search.feign.ProductFiegnService;
import edu.hubu.mall.search.service.MallSearchService;
import edu.hubu.mall.search.vo.SearchParamVo;
import edu.hubu.mall.search.vo.SearchResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.lucene.search.TotalHits;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static edu.hubu.mall.search.config.MallElasticConfig.COMMON_OPTIONS;

/**
 * @Author: huxiaoge
 * @Date: 2021-05-12
 * @Description: ????????????
 **/
@Service
@Slf4j
public class MallSearchServiceImpl implements MallSearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Autowired
    private ProductFiegnService productFiegnService;

    /**
     * ????????????????????????????????????
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
     * ??????????????????
     */
    private SearchRequest buildSearchRequest(SearchParamVo param){

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();

        //1.??????Query
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //1.1 mustQuery  ??????skuTitle??????
        if(StringUtils.isNotEmpty(param.getKeyword())){
            boolQuery.must(QueryBuilders.matchQuery("skuTitle",param.getKeyword()));
        }
        //1.2 filterQuery ??????catalog3Id
        if(null != param.getCatalog3Id()){
            boolQuery.filter(QueryBuilders.termQuery("catalogId",param.getCatalog3Id()));
        }

        //1.3 filterQuery ??????????????????id??????
        if(!CollectionUtils.isEmpty(param.getBrandId())){
            boolQuery.filter(QueryBuilders.termsQuery("brandId",param.getBrandId()));
        }

        // 1.4 filterQuery ????????????attrs?????? nestQuery  ??????attrs=1_ios:android
        if(!CollectionUtils.isEmpty(param.getAttrs())){
            for(String attrStr: param.getAttrs()){

                BoolQueryBuilder nestedboolQuery = QueryBuilders.boolQuery();
                String[] attrArr = attrStr.split("_");
                String attrId = attrArr[0];
                String[] attrVals = attrArr[1].split(":");
                nestedboolQuery.must(QueryBuilders.termQuery("attrs.attrId",attrId));
                nestedboolQuery.must(QueryBuilders.termsQuery("attrs.attrValue",attrVals));

                //?????????????????????????????????nested??????
                NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", nestedboolQuery, ScoreMode.None);
                boolQuery.filter(nestedQuery);
            }

        }

        //1.5 filterQuery ?????????????????????
        if(null != param.getHasStock()){
            boolQuery.filter(QueryBuilders.termQuery("hasStock",param.getHasStock() == 1));
        }

        //1.6 filterQuery ????????????
        if(StringUtils.isNotEmpty(param.getSkuPrice())){
            RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery("skuPrice");
            String skuPrice = param.getSkuPrice();
            String[] priceRange = skuPrice.split("_");
            if(skuPrice.startsWith("_")){
                rangeQuery.lte(priceRange[1]);
            }else if(skuPrice.endsWith("_")){
                rangeQuery.gte(priceRange[0]);
            }else{   //????????????????????????????????????
                rangeQuery.gte(priceRange[0]).lte(priceRange[1]);
            }
            boolQuery.filter(rangeQuery);
        }

        //??????????????????
        sourceBuilder.query(boolQuery);

        //????????????????????????????????????
        //2.1 ??????
        if(StringUtils.isNotEmpty(param.getSort())){
            String sortStr = param.getSort();
            String[] sortArr = sortStr.split("_");
            String sortField = sortArr[0];   //????????????
            SortOrder sortOrder  = sortArr[1].equalsIgnoreCase("asc" ) ? SortOrder.ASC : SortOrder.DESC;  //?????????
            sourceBuilder.sort(sortField,sortOrder);
        }

        //2.2 ??????
        Integer pageNum = param.getPageNum();
        int from = (pageNum - 1) * ElasticConstant.PRODUCT_ES_PAGESIZE;
        sourceBuilder.from(from);
        sourceBuilder.size(ElasticConstant.PRODUCT_ES_PAGESIZE);

        //2.3 ??????
        if(StringUtils.isNotEmpty(param.getKeyword())){
            HighlightBuilder builder = new HighlightBuilder();
            builder.field("skuTitle");
            builder.preTags("<b style='color:red'>");
            builder.postTags("</b>");
            sourceBuilder.highlighter(builder);
        }

        /**
         * 3.????????????
         */
        //3.1????????????
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg");
        brand_agg.field("brandId").size(50);
        //????????????????????????,???????????????????????????
        brand_agg.subAggregation(AggregationBuilders.terms("brand_name_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brand_img_agg").field("brandImg").size(1));

        sourceBuilder.aggregation(brand_agg);

        //3.2????????????
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg");
        catalog_agg.field("catalogId").size(1);
        //?????????????????????????????????
        catalog_agg.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName").size(1));

        sourceBuilder.aggregation(catalog_agg);

        //3.3????????????,nest???????????????
        NestedAggregationBuilder nested = AggregationBuilders.nested("attr_agg", "attrs");
        TermsAggregationBuilder attr_id_agg = AggregationBuilders.terms("attr_id_agg");
        attr_id_agg.field("attrs.attrId").size(10);
        //??????????????? --?????????????????????
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_name_agg").field("attrs.attrName").size(10));
        attr_id_agg.subAggregation(AggregationBuilders.terms("attr_value_agg").field("attrs.attrValue").size(50));
        nested.subAggregation(attr_id_agg);
        sourceBuilder.aggregation(nested);

        String dslStr = sourceBuilder.toString();
        log.info("?????????dsl????????????{}",dslStr);

        return new SearchRequest(new String[]{ElasticConstant.PRODUCT_ES_INDEX},sourceBuilder);
    }

    /**
     * ??????????????????
     * @param response
     * @return
     */
    private SearchResultVo buildSearchResult(SearchResponse response,SearchParamVo searchParam) {

        SearchResultVo result = new SearchResultVo();
        //?????????????????????
        SearchHits hits = response.getHits();
        TotalHits totalHits = hits.getTotalHits();//????????????
        long total = totalHits.value;
        result.setTotal(total);
        //?????????
        int totalPages = (int)total % ElasticConstant.PRODUCT_ES_PAGESIZE == 0 ?
                (int) total / ElasticConstant.PRODUCT_ES_PAGESIZE : (int)total / ElasticConstant.PRODUCT_ES_PAGESIZE + 1;
        result.setTotalPages(totalPages);
        //?????????
        result.setPageNum(searchParam.getPageNum());

        List<SkuEsModel> esSkus = new ArrayList<>();
        //?????????????????????????????????
        if(hits.getHits() != null && hits.getHits().length > 0){
            for (SearchHit hit:hits.getHits()) {
                String sourceStr = hit.getSourceAsString();
                SkuEsModel esModel = JSON.parseObject(sourceStr, SkuEsModel.class);
                //????????????
                if(StringUtils.isNotEmpty(searchParam.getKeyword())){
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String highlightTitle = skuTitle.fragments()[0].string();
                    esModel.setSkuTitle(highlightTitle);
                }
                esSkus.add(esModel);
            }
        }
        result.setProducts(esSkus);

        //????????????????????????
        Aggregations aggregations = response.getAggregations();

        //??????????????????
        ParsedLongTerms catalog_agg = aggregations.get("catalog_agg");
        List<SearchResultVo.CatalogVo> catalogs = new ArrayList<>();
        List<? extends Terms.Bucket> buckets = catalog_agg.getBuckets();
        for(Terms.Bucket bucket:buckets){
            SearchResultVo.CatalogVo catalogVo = new SearchResultVo.CatalogVo();
            catalogVo.setCatalogId(Long.parseLong(bucket.getKeyAsString()));  //???????????????id

            ParsedStringTerms catalog_name_agg = bucket.getAggregations().get("catalog_name_agg");
            String catalogName = catalog_name_agg.getBuckets().get(0).getKeyAsString();

            catalogVo.setCatalogName(catalogName);
            catalogs.add(catalogVo);
        }
        result.setCatalogs(catalogs);

        //??????????????????
        ParsedLongTerms brand_agg = aggregations.get("brand_agg");
        List<? extends Terms.Bucket> brandBuckets = brand_agg.getBuckets();
        List<SearchResultVo.BrandVo> brands = new ArrayList<>();
        for(Terms.Bucket bucket:brandBuckets){
            SearchResultVo.BrandVo brandVo = new SearchResultVo.BrandVo();
            brandVo.setBrandId(Long.parseLong(bucket.getKeyAsString()));   //??????Id
            //???????????????
            ParsedStringTerms brand_name_agg = bucket.getAggregations().get("brand_name_agg");
            String brandName = brand_name_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);
            //???????????????
            ParsedStringTerms brand_img_agg = bucket.getAggregations().get("brand_img_agg");
            String brandImg = brand_img_agg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);
            brands.add(brandVo);
        }
        result.setBrands(brands);

        //???????????????
        ParsedNested attrs_agg = aggregations.get("attr_agg");
        ParsedLongTerms attr_id_agg = attrs_agg.getAggregations().get("attr_id_agg");
        List<SearchResultVo.AttrVo> attrs = new ArrayList<>();
        List<? extends Terms.Bucket> attrBucket = attr_id_agg.getBuckets();
        for(Terms.Bucket bucket:attrBucket){
            SearchResultVo.AttrVo attrVo = new SearchResultVo.AttrVo();
            attrVo.setAttrId(Long.parseLong(bucket.getKeyAsString()));
            //?????????
            ParsedStringTerms attr_name_agg = bucket.getAggregations().get("attr_name_agg");
            String attrName = attr_name_agg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);
            //?????????
            ParsedStringTerms attr_value_agg = bucket.getAggregations().get("attr_value_agg");
            List<? extends Terms.Bucket> attrValue = attr_value_agg.getBuckets();
            List<String> attrValues = attrValue.stream().map(MultiBucketsAggregation.Bucket::getKeyAsString).collect(Collectors.toList());
            attrVo.setAttrValues(attrValues);

            attrs.add(attrVo);
        }
        result.setAttrs(attrs);

        /**
         * ??????????????????????????????
         */
        List<Integer> pages = new ArrayList<>();
        for(int i =1; i <= totalPages;i++){
            pages.add(i);
        }
        result.setPageNavs(pages);

        /**
         * ??????????????????????????????
         * ????????????
         */
        List<SearchResultVo.BreadVo> breads = new ArrayList<>();
        if(!CollectionUtils.isEmpty(searchParam.getAttrs())){
            List<Long> attrIds = searchParam.getAttrs().stream().map(attr -> {
                String[] attrStr = attr.split("_");
                return Long.parseLong(attrStr[0]);
            }).collect(Collectors.toList());

            //???????????????????????????id
            result.setAttrIds(attrIds);

            List<AttrEntity> attrEntities = productFiegnService.queryAttrsByIds(attrIds);

            breads = searchParam.getAttrs().stream().map(attr -> {
                SearchResultVo.BreadVo breadVo = new SearchResultVo.BreadVo();
                String[] s = attr.split("_");
                //????????????
                breadVo.setBreadValue(s[1]);
                //?????????
                List<AttrEntity> filterAttr = attrEntities.stream().filter(item -> item.getAttrId().equals(Long.parseLong(s[0]))).collect(Collectors.toList());
                breadVo.setBreadName(CollectionUtils.isEmpty(filterAttr) ? "" : filterAttr.get(0).getAttrName());

                //????????????????????????????????????????????????
                //??????????????????????????????????????????????????????
                String replace = replaceQueryString(searchParam, "attrs", attr, s[0]);
                breadVo.setLink("http://search.emall.com/list.html?" + replace);

                return breadVo;
            }).collect(Collectors.toList());
        }

        /**
         * ??????????????????????????????
         * ????????????
         */
        if(!CollectionUtils.isEmpty(searchParam.getBrandId())){
            //????????????
            List<BrandEntity> brandEntities = productFiegnService.queryBrandByIds(searchParam.getBrandId());
            String brandStr = brandEntities.stream().map(BrandEntity::getName).collect(Collectors.joining(";"));

            SearchResultVo.BreadVo breadVo = new SearchResultVo.BreadVo();
            breadVo.setBreadName("??????");
            breadVo.setBreadValue(brandStr);
            String replace = "";
            for(BrandEntity brand:brandEntities){
               replace =  replaceQueryString(searchParam,"brandId",brand.getBrandId() + "",brand.getBrandId() + "");
            }
            breadVo.setLink("http://search.emall.com/list.html?" + replace);
            breads.add(breadVo);
        }
        result.setBreads(breads);

        return result;
    }

    /**
     * ??????????????????????????????
     * @param param
     * @param key
     * @param value
     * @param flag ??????????????????????????????????????????????????????????????????15_CPU?????????????????????15
     * @return
     */
    private String replaceQueryString(SearchParamVo param,String key,String value,String flag){
        String encode = null;
        try {
            encode = URLEncoder.encode(value, "UTF-8");
            encode = encode.replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        //???????????????????????????????????????????????????????????????
        int i = param.get_queryString().indexOf(key + "=" + flag);
        String replace = "";
        if(i == 0){
            replace = param.get_queryString().replace(key + "=" + encode, "");
        }else{
            replace = param.get_queryString().replace("&" + key + "=" + encode, "");
        }
        return replace;
    }

}
