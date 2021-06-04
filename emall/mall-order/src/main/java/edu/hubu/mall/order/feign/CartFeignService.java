package edu.hubu.mall.order.feign;

import edu.hubu.mall.common.order.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @Description: 远程查询购物车的数据
 * @Author: huxiaoge
 * @Date: 2021-06-04
 **/
@FeignClient("mall-cart")
public interface CartFeignService {

    @GetMapping("/cart/CartItems")
    List<OrderItemVo> queryMemberCartItems();
}
