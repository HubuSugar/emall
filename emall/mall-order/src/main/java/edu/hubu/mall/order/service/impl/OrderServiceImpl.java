package edu.hubu.mall.order.service.impl;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import edu.hubu.mall.order.entity.OrderEntity;
import edu.hubu.mall.order.entity.OrderReturnReasonEntity;
import edu.hubu.mall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-03
 **/
@Service
//@RabbitListener(queues = {"hello"})
@Slf4j
public class OrderServiceImpl implements OrderService {


//    @RabbitHandler
    public void receiveMessage(OrderReturnReasonEntity returnReasonEntity){
        log.info("消息内容 ===》" + returnReasonEntity.toString());
    }

//    @RabbitHandler
    public void receiveMessage(OrderEntity orderEntity){
        log.info("消息内容 ===》" + orderEntity.toString());
    }
}
