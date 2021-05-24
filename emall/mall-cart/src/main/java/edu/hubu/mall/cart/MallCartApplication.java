package edu.hubu.mall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: huxiaoge
 * @Date: 2021/5/24
 * @Description:
 **/
@EnableDiscoveryClient
@EnableFeignClients
@SpringBootApplication
public class MallCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallCartApplication.class,args);
    }
}
