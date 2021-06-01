package edu.hubu.mall.cart.feign;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.product.SkuInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-01
 **/
@FeignClient("mall-product")
public interface ProductFeignService {

    @GetMapping("/product/skuInfo/querySkuInfo/{skuId}")
    SkuInfoVo querySkuInfoById(@PathVariable("skuId") Long skuId);

    @GetMapping("/product/saleAttr/querySkuSaleAttrValues/{skuId}")
    List<String> querySkuSaleAttrValues(@PathVariable("skuId") Long skuId);

}
