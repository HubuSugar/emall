package edu.hubu.mall.seckill.feign;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.product.SkuInfoVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Set;

/**
 * @Author: huxiaoge
 * @Date: 2021-07-12
 * @Description:
 **/
@FeignClient("mall-product")
public interface ProductFeignService {

    @GetMapping("/product/skuInfo/querySkuInfos")
    Result<List<SkuInfoVo>> querySkuInfoBatch(@RequestParam("ids") Set<Long> ids);
}
