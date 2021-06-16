package edu.hubu.mall.ware.config;

import edu.hubu.mall.common.constant.WareConstant;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author: huxiaoge
 * @Date: 2021-06-15
 * @Description:
 **/
@Configuration
public class MyRabbitConfig {

    /**
     * 消息的序列化机制
     */
    @Bean
    public MessageConverter messageConverter(){
        return new Jackson2JsonMessageConverter();
    }

    /**
     * 创建交换机
     */
    @Bean
    public Exchange stockEventChange(){
        return new TopicExchange(WareConstant.WARE_EVENT_EXCHANGE,true,false);
    }

    /**
     * 创建库存释放队列
     */
    @Bean
    public Queue stockReleaseQueue(){
        return new Queue(WareConstant.WARE_RELEASE_QUEUE,true,false,false);
    }

    /**
     * 库存锁定成功消息发给延迟队列
     */
    @Bean
    public Queue stockDelayQueue(){
        Map<String,Object> args = new HashMap<>();
        args.put("x-dead-letter-exchange",WareConstant.WARE_EVENT_EXCHANGE);
        args.put("x-dead-letter-routing-key",WareConstant.WARE_STOCK_RELEASE_ROUTE);
        args.put("x-message-ttl",WareConstant.WARE_RELEASE_TIMEOUT);
        return new Queue(WareConstant.WARE_DELAY_QUEUE,true,false,false,args);
    }

    @Bean
    public Binding stockLockedBinding(){
        return new Binding(WareConstant.WARE_DELAY_QUEUE, Binding.DestinationType.QUEUE,WareConstant.WARE_EVENT_EXCHANGE,WareConstant.WARE_STOCK_LOCKED_ROUTE,null);
    }

    @Bean
    public Binding stockReleaseBinding(){
        return new Binding(WareConstant.WARE_RELEASE_QUEUE, Binding.DestinationType.QUEUE,WareConstant.WARE_EVENT_EXCHANGE,WareConstant.WARE_STOCK_RELEASE_ROUTE,null);
    }

}
