package edu.hubu.mall.ware.listener;

import com.rabbitmq.client.Channel;
import edu.hubu.mall.common.constant.WareConstant;
import edu.hubu.mall.common.mq.StockLockTo;
import edu.hubu.mall.common.order.OrderVo;
import edu.hubu.mall.ware.service.WareSkuService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * @Description: 专门用来监听消息队列的服务,库存释放的消息队列中存放了两种消息
 * @Author: huxiaoge
 * @Date: 2021-06-19
 **/
@Slf4j
@Service
@RabbitListener(queues = WareConstant.WARE_RELEASE_QUEUE)
public class StockReleaseListener {

    @Autowired
    private WareSkuService wareSkuService;

    @RabbitHandler
    public void handleStockLockedRelease(StockLockTo stockLockTo, Message message, Channel channel) throws IOException {
        log.info("收到库存解锁的消息...." + stockLockTo.getTaskId() + stockLockTo.getTaskDetail().getSkuName());
        try{
            wareSkuService.unlockStock(stockLockTo);

            //手动确认消息(不批量确认)
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            //出现异常消息重新入队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
    }

    @RabbitHandler
    public void handleOrderRelease(OrderVo order,Message message,Channel channel) throws IOException {
        log.info("收到订单关单后，解锁库存的消息...." + order.getOrderSn());
        try{
            wareSkuService.unlockStock(order);
            //手动确认消息(不批量确认)
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        }catch (Exception e){
            //出现异常消息重新入队
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }

    }
}
