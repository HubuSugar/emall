package edu.hubu.mall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @Author: huxiaoge
 * @Date: 2021/5/24
 * @Description:
 **/
@EnableRedisHttpSession
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "edu.hubu.mall.cart.feign")
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class MallCartApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallCartApplication.class,args);
    }
}
