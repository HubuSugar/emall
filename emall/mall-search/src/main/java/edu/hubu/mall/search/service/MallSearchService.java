package edu.hubu.mall.search.service;

import edu.hubu.mall.search.vo.SearchParamVo;
import edu.hubu.mall.search.vo.SearchResultVo;

/**
 * @Author: huxiaoge
 * @Date: 2021-05-12
 * @Description: 搜索服务
 **/
public interface MallSearchService {
    /**
     * 根据参数查询商品搜索结果
     * @param param
     * @return
     */
    SearchResultVo skuSearch(SearchParamVo param);
}
