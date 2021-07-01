package edu.hubu.mall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-21
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableRedisHttpSession
@EnableFeignClients(basePackages = "edu.hubu.mall.member.feign")
public class MallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallMemberApplication.class,args);
    }
}
