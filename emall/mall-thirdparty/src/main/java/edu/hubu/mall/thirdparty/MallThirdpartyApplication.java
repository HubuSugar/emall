package edu.hubu.mall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Description: 第三方服务
 * @Author: huxiaoge
 * @Date: 2021-05-21
 **/
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
public class MallThirdpartyApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallThirdpartyApplication.class,args);
    }
}
