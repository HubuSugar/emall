package edu.hubu.mall.search.controller;

import edu.hubu.mall.search.service.MallSearchService;
import edu.hubu.mall.search.vo.SearchParamVo;
import edu.hubu.mall.search.vo.SearchResultVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author: huxiaoge
 * @Date: 2021-05-12
 * @Description: 商品搜索服务
 **/
@Controller
public class SearchController {

    @Autowired
    private MallSearchService mallSearchService;

    /**
     * 搜索首页
     * @param param
     * @param model
     * @return
     */
    @GetMapping({"/list.html","/"})
    public String searchSkuByCondition(SearchParamVo param, Model model, HttpServletRequest request){

        /**
         * 到es中检索商品
         */
        String queryString = request.getQueryString();  //获取到请求的查询参数
        param.set_queryString(queryString);

        SearchResultVo searchResultVo =  mallSearchService.skuSearch(param);
        model.addAttribute("result",searchResultVo);
        return "list";
    }

}
