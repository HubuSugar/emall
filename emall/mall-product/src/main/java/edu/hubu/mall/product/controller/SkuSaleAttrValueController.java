package edu.hubu.mall.product.controller;

import edu.hubu.mall.product.service.SkuSaleAttrValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-01
 **/
@RestController
@RequestMapping("/product/saleAttr")
public class SkuSaleAttrValueController {

    @Autowired
    SkuSaleAttrValueService saleAttrValueService;

    @GetMapping("/querySkuSaleAttrValues/{skuId}")
    List<String> querySkuSaleAttrValues(@PathVariable("skuId") Long skuId){
        return saleAttrValueService.querySkuSaleAttrValuesAsString(skuId);
    }

}
