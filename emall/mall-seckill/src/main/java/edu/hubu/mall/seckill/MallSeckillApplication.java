package edu.hubu.mall.seckill;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-07-11
 **/
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "edu.hubu.mall.seckill.feign")
public class MallSeckillApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallSeckillApplication.class,args);
    }
}
