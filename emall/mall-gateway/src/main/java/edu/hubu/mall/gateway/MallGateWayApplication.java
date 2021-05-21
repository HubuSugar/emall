package edu.hubu.mall.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Author: huxiaoge
 * @Date: 2021/04/27
 * @Desccription: gateway网关服务
 *
 * common项目pom文件中引入了mybatisplus组件,并且网关项目不需要配置数据源
 * 但是springboot项目启动时会首先检查数据源的自动注入，所以需要先排除数据源注入
 */
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
public class MallGateWayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallGateWayApplication.class,args);
    }
}
