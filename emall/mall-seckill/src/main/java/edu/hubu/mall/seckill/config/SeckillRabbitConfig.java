package edu.hubu.mall.seckill.config;

import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-07-15
 **/
@Configuration
public class SeckillRabbitConfig {
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
}
