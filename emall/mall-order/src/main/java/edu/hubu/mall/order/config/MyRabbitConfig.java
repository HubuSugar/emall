package edu.hubu.mall.order.config;

import com.rabbitmq.client.Channel;
import edu.hubu.mall.common.constant.OrderConstant;
import edu.hubu.mall.common.constant.WareConstant;
import edu.hubu.mall.order.entity.OrderEntity;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.api.ChannelAwareBatchMessageListener;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @Description: mq配置
 * @Author: huxiaoge
 * @Date: 2021-06-03
 **/
@Configuration
public class MyRabbitConfig {

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Primary
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory){
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);

        /**
         * 设置消息的序列化器
         */
        rabbitTemplate.setMessageConverter(messageConverter());

        /**
         *  设置confirmCallback
         *  消息抵达交换机的回调
         *  correlationData
         *  ack:是否确认
         *  cause
         */
        rabbitTemplate.setConfirmCallback((CorrelationData correlationData, boolean ack, String cause) -> {
            System.out.println("confirm-->correlationData:" + correlationData + "--> ack:" + ack + "--> cause:" + cause);
        });

        /**
         * 设置returnCallback
         *  消息路由到消息队列异常时回调
         *  只要消息没有投递给指定的队列，就触发这个失败回调
         *  message：投递失败的消息详细信息
         *  replyCode：回复的状态码
         *  replyText：回复的文本内容
         *  exchange：当时这个消息发给哪个交换机
         *  routingKey：当时这个消息用哪个路邮键
         */
//        rabbitTemplate.setReturnCallback((message,replyCode,replyText,exchange,routingKey)->{
//            System.out.println(message.toString() + replyCode + replyText + exchange + routingKey);
//        });

        return rabbitTemplate;
    }

    /**
     * 创建订单需要的mq配置
     *  1、创建订单交换机
     */
    @Bean
    public TopicExchange orderEventChange(){
        return new TopicExchange(OrderConstant.ORDER_EVENT_EXCHANGE,true,false,new HashMap<>());
    }

    /**
     * 2、创建死信队列
     * @return
     */
    @Bean
    public Queue orderDelayQueue(){
        Map<String,Object> delayArgs = new HashMap<>();
        delayArgs.put("x-dead-letter-exchange",OrderConstant.ORDER_EVENT_EXCHANGE);
        delayArgs.put("x-dead-letter-routing-key",OrderConstant.ORDER_RELEASE_ROUTE);
        delayArgs.put("x-message-ttl",OrderConstant.ORDER_RELEASE_TIMEOUT);
        return new Queue(OrderConstant.ORDER_DELAY_QUEUE,true,false,false,delayArgs);
    }

    /**
     * 3、创建交换机和死信队列的绑定关系
     * @return
     */
    @Bean
    public Binding orderCreateBinding(){
        return new Binding(OrderConstant.ORDER_DELAY_QUEUE, Binding.DestinationType.QUEUE,OrderConstant.ORDER_EVENT_EXCHANGE,
                OrderConstant.ORDER_CREATE_ROUTE,new HashMap<>());
    }

    /**
     * 创建创建订单释放后进入的队列
     * @return
     */
    @Bean
    public Queue orderReleaseQueue(){
        return new Queue(OrderConstant.ORDER_RELEASE_QUEUE,true,false,false,null);
    }

    /**
     * 创建订单从死信队列释放后的绑定关系
     */
    @Bean
    public Binding orderReleaseBinding(){
        return new Binding(OrderConstant.ORDER_RELEASE_QUEUE, Binding.DestinationType.QUEUE,OrderConstant.ORDER_EVENT_EXCHANGE,
                OrderConstant.ORDER_RELEASE_ROUTE,new HashMap<>());
    }

    /**
     * 订单关闭后，向库存服务发送消息
     * @return
     */
    @Bean
    public Binding orderReleaseOtherBinding() {
        return new Binding(WareConstant.WARE_RELEASE_QUEUE,
                Binding.DestinationType.QUEUE,
                OrderConstant.ORDER_EVENT_EXCHANGE,
                OrderConstant.ORDER_RELEASE_OTHER_ROUTE,
                null);
    }

    /**
     * 秒杀创建订单队列
     */
    @Bean
    public Queue seckillOrderQueue(){
        return new Queue(OrderConstant.ORDER_SECKILL_QUEUE,true,false,false,null);
    }

    /**
     * 秒杀交换机和队列绑定
     */
    @Bean
    public Binding seckillOrderBinding(){
        return new Binding(OrderConstant.ORDER_SECKILL_QUEUE, Binding.DestinationType.QUEUE,OrderConstant.ORDER_EVENT_EXCHANGE,
                OrderConstant.ORDER_SECKILL_ROUTE,null);
    }

}
