package edu.hubu.mall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-21
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "edu.hubu.mall.member.feign")
public class MallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallMemberApplication.class,args);
    }
}
