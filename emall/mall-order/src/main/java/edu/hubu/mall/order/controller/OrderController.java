package edu.hubu.mall.order.controller;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.auth.MemberVo;
import edu.hubu.mall.common.order.OrderVo;
import edu.hubu.mall.common.utils.PageUtil;
import edu.hubu.mall.order.Interceptor.LoginRequireInterceptor;
import edu.hubu.mall.order.entity.OrderEntity;
import edu.hubu.mall.order.service.OrderService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/3
 * @Description:
 **/
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @GetMapping("/order/info/{orderSn}")
    OrderVo queryInfoByOrderSn(@PathVariable("orderSn") String orderSn){
        return orderService.queryInfoByOrderSn(orderSn);
    }

    /**
     * 分页查询当前用户的订单记录
     */
    @PostMapping("/order/memberOrders")
    Result<PageUtil<OrderVo>> queryMemberOrderList(@RequestBody Map<String,Object> args){
        Result<PageUtil<OrderVo>> ok = Result.ok();
        PageUtil<OrderVo> data = orderService.queryMemberOrderList(args);
        ok.setData(data);
        return ok;
    }


}
