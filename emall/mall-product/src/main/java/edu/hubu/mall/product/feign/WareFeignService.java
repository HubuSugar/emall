package edu.hubu.mall.product.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description: 库存服务的feign客户端
 **/
@FeignClient(value = "mall-ware")
public interface WareFeignService {

    @PostMapping(value="/ware/wareSku/hasStock")
    Map<Long, Boolean> getSkuHasStock(@RequestBody List<Long> skuIds);
}
