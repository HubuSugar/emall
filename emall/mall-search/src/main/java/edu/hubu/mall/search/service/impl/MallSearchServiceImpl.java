package edu.hubu.mall.search.service.impl;

import edu.hubu.mall.search.service.MallSearchService;
import edu.hubu.mall.search.vo.SearchParamVo;
import edu.hubu.mall.search.vo.SearchResultVo;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        new SearchRequest();

        return null;
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
