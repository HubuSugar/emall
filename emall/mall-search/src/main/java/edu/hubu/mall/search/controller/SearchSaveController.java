package edu.hubu.mall.search.controller;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.exception.BizCodeEnum;
import edu.hubu.mall.common.es.SkuEsModel;
import edu.hubu.mall.search.service.SearchSaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
@RestController
@RequestMapping("/search/save")
public class SearchSaveController {

    @Autowired
    private SearchSaveService searchSaveService;

    /**
     * 将要上架的商品数据保存到es中
     */
    @PostMapping("/spuInfo")
    public Result searchSpuUp(@RequestBody List<SkuEsModel> esModelList){
        boolean saveRes;
        try{
            saveRes = searchSaveService.saveSpuInfoList(esModelList);
        }catch (IOException e){
            return Result.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }
        if(saveRes){
            return Result.error(BizCodeEnum.PRODUCT_UP_EXCEPTION.getCode(),BizCodeEnum.PRODUCT_UP_EXCEPTION.getMsg());
        }else{
            return Result.ok();
        }
    }
}
