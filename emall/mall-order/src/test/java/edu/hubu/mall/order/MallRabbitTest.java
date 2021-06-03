package edu.hubu.mall.order;

import com.alibaba.fastjson.JSON;
import edu.hubu.mall.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-03
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class MallRabbitTest {

    @Autowired
    AmqpAdmin admin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 单播模式
     *
     */
    @Test
    public void createDirectExechange(){
        Exchange directExchage = ExchangeBuilder.directExchange("exchage.direct").build();
        admin.declareExchange(directExchage);
        log.info("创建直接交换机完成{}","exchage.direct");
    }

    /**
     * 广播模式
     * 消息会发给绑定的所有队列
     */
    @Test
    public void createFanoutExechange(){
        Exchange fanoutExchage = ExchangeBuilder.fanoutExchange("exchage.fanout").build();
        admin.declareExchange(fanoutExchage);
        log.info("创建广播交换机完成{}","exchage.fanout");
    }

    /**
     * 部分广播模式
     * 区分路由
     * 支持*、#通配符，单词匹配
     * *匹配一个单词，#匹配0个或多个单词
     */
    @Test
    public void createTopExechange(){
        Exchange topicExchage = ExchangeBuilder.topicExchange("exchage.topic").build();
        admin.declareExchange(topicExchage);
        log.info("创建主题交换机完成{}","exchage.topic");
    }

    @Test
    public void createQueue(){
        Queue queue = new Queue("queue.java");
        admin.declareQueue(queue);
        log.info("创建队列成功{}","queue.java");
    }

    @Test
    public void decalerBind(){
        Binding binding = new Binding("queue.java", Binding.DestinationType.QUEUE, "exchage.fanout", "fanout.java", null);
        admin.declareBinding(binding);
        log.info("绑定成功");
    }

    @Test
    public void sendMessage(){

        Result error = Result.error(211, "错误");
        String s = JSON.toJSONString(error);
        rabbitTemplate.convertAndSend("exchage.fanout","fanout.java", s);

    }
}
