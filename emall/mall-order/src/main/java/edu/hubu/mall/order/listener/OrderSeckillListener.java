package edu.hubu.mall.order.listener;

import com.rabbitmq.client.Channel;
import edu.hubu.mall.common.constant.OrderConstant;
import edu.hubu.mall.common.seckill.SeckillOrderTo;
import edu.hubu.mall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-07-15
 **/
@Slf4j
@Service
@RabbitListener(queues = {OrderConstant.ORDER_SECKILL_QUEUE})
public class OrderSeckillListener {

    @Autowired
    OrderService orderService;

    @RabbitHandler
    public void seckillOrderHandle(SeckillOrderTo orderTo, Message message, Channel channel){
        log.info("秒杀下单成功，开始创建订单");

    }
}
