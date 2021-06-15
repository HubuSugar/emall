package edu.hubu.mall.ware;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description: 库存服务
 **/
@EnableRabbit
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "edu.hubu.mall.ware.feign")
public class MallWareApplication {
    public static void main(String[] args) {
        SpringApplication.run(MallWareApplication.class,args);
    }
}
