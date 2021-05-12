package edu.hubu.mall.search.service.impl;

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
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;

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
            searchResult =  buildSearchResult(search);
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

        // 1.4 filterQuery 根据属性attrs查询 nestQuery  格式 1_ios:android
        if(!CollectionUtils.isEmpty(param.getAttrs())){
            NestedQueryBuilder nestedQuery = QueryBuilders.nestedQuery("attrs", null, ScoreMode.None);
            boolQuery.filter(nestedQuery);
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

        sourceBuilder.query(boolQuery);

        //2.排序


        return new SearchRequest(new String[]{ElasticConstant.PRODUCT_ES_INDEX},sourceBuilder);
    }

    /**
     * 处理返回结果
     * @param search
     * @return
     */
    private SearchResultVo buildSearchResult(SearchResponse search) {



        return null;
    }


}
