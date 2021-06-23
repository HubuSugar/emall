package edu.hubu.mall.order.listener;

import com.rabbitmq.client.Channel;
import edu.hubu.mall.common.constant.OrderConstant;
import edu.hubu.mall.order.entity.OrderEntity;
import edu.hubu.mall.order.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Description: 监听订单释放队列
 * @Author: huxiaoge
 * @Date: 2021-06-19
 **/
@Slf4j
@Service
@RabbitListener(queues = OrderConstant.ORDER_RELEASE_QUEUE)
public class OrderCloseListener {

    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void handleCloseOrder(OrderEntity order, Message message, Channel channel) throws IOException {
        log.info("收到关单请求...." + order.getOrderSn());
        try{
            orderService.closeOrder(order);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }

}
