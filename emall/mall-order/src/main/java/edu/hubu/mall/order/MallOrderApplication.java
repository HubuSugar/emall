package edu.hubu.mall.order;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/3
 * @Description: 订单服务
 * 使用 @RabbitListener 监听消息队列的消息
 * 使用 @RabbitHandler 监听不同类型的消息
 * 消息的可靠抵达
 * 发送端的回调： confirmCallback   未投递到交换机
 *              returnCallback   交换机未投递到队列
 * 消费端的ack
 **/
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableRabbit
public class MallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallOrderApplication.class,args);
    }
}
