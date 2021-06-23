package edu.hubu.mall.ware.feign;

import edu.hubu.mall.common.order.OrderVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-17
 **/
@FeignClient("mall-order")
public interface OrderFeignService {

    @GetMapping("/order/order/info/{orderSn}")
    OrderVo queryInfoByOrderSn(@PathVariable("orderSn") String orderSn);

}
