package edu.hubu.mall.product.controller;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.product.SkuInfoVo;
import edu.hubu.mall.product.entity.SkuInfoEntity;
import edu.hubu.mall.product.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
@RestController
@RequestMapping("/product/skuInfo")
public class SkuInfoController {

    @Autowired
    private SkuInfoService skuInfoService;

    /**
     * 根据skuId查询sku_info
     */
    @GetMapping("/querySkuInfo/{skuId}")
    public SkuInfoVo querySkuInfoById(@PathVariable("skuId") Long skuId){
        return skuInfoService.querySkuInfoById(skuId);
    }

}
