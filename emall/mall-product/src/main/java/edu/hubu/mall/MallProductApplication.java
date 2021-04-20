package edu.hubu.mall;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/20
 * @Description: 商品服务
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class MallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallProductApplication.class,args);
    }

}
