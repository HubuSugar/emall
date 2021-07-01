package edu.hubu.mall.member.feign;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.order.OrderVo;
import edu.hubu.mall.common.utils.PageUtil;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-04
 **/
@FeignClient("mall-order")
public interface OrderFeignService {

    /**
     * 查询用户的所有订单记录
     */
    @PostMapping("/order/order/memberOrders")
    Result<PageUtil<OrderVo>> queryMemberOrderList(@RequestBody Map<String,Object> args);

}
