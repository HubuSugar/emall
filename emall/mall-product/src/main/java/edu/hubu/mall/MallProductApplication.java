package edu.hubu.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/20
 * @Description: 商品服务
 * 开启feign的自动注入
 *
 * 7.整合redisson作为分布式的框架
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "edu.hubu.mall.feign")
public class MallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallProductApplication.class,args);
    }

}
