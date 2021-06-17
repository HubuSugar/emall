package edu.hubu.mall.order.controller;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.order.OrderVo;
import edu.hubu.mall.order.entity.OrderEntity;
import edu.hubu.mall.order.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/info/{orderSn}")
    OrderVo queryInfoByOrderSn(@PathVariable("orderSn") String orderSn){
        return orderService.queryInfoByOrderSn(orderSn);
    }


}
