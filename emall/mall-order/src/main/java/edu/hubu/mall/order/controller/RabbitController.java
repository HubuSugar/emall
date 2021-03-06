package edu.hubu.mall.order.controller;

import cn.hutool.core.util.IdUtil;
import edu.hubu.mall.common.constant.OrderConstant;
import edu.hubu.mall.order.entity.OrderEntity;
import edu.hubu.mall.order.entity.OrderReturnReasonEntity;
import edu.hubu.mall.order.service.OrderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-03
 **/
@RestController
public class RabbitController {

    @Autowired
    RabbitTemplate rabbitTemplate;


    @GetMapping("/order/send")
    public String sendMessage(){
        for(int i = 0; i< 10;i++){
            if(i % 2 == 0){
                OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
                orderReturnReasonEntity.setOrderId(1L);
                orderReturnReasonEntity.setId((long) i);
                orderReturnReasonEntity.setName("哈哈" + i);
                rabbitTemplate.convertAndSend("exchange.direct","hello.direct",orderReturnReasonEntity);
            }else{
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setId((long)i);
                orderEntity.setOrderSn(IdUtil.simpleUUID());
                rabbitTemplate.convertAndSend("exchange.direct","hello2.direct",orderEntity);
            }
        }
        return "ok";
    }


    @GetMapping("/test/createOrder")
    public String createOrder(){
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(IdUtil.simpleUUID());
        rabbitTemplate.convertAndSend(OrderConstant.ORDER_EVENT_EXCHANGE,OrderConstant.ORDER_CREATE_ROUTE,orderEntity);
        return "ok";
    }


}
