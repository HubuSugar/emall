package edu.hubu.mall.order.feign;

import edu.hubu.mall.common.product.SpuInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/5
 * @Description:
 **/
@FeignClient("mall-product")
public interface ProductFeignService {

    /**
     * 根据skuId查询spuinfo信息
     */
    @GetMapping("/product/spuInfo/{id}/info")
    SpuInfoVo querySpuInfoBySkuId(@PathVariable("id") Long skuId);
}
