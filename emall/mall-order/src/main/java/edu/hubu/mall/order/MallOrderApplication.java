package edu.hubu.mall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/3
 * @Description:
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class MallOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallOrderApplication.class,args);
    }
}
