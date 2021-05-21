package edu.hubu.mall.fast;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/22
 * @Description: 后台管理系统
 **/
@SpringBootApplication
@EnableDiscoveryClient
public class MallFastApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallFastApplication.class,args);
    }
}
