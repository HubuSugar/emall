package edu.hubu.mall.product;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/20
 * @Description: 商品服务
 * 开启feign的自动注入
 *
 * 7.整合redisson作为分布式的框架
 * 8.整合spring-cache缓存
 * 引入spring-boot-starter-cache后自动注入了 CacheAutoConfiguration -> RedisCacheConfiguration ->然后自动配置了RedisCacheManager ->初始化所有缓存（每个缓存决定使用什么配置）
 * -> 如果 RedisCacheConfiguration有就用已有的，没有就用默认
 * -> 想改缓存的配置,只需要给容器中放一个RedisCacheConfiguration即可
 * -> 就会应用到当前RedisCacheManager管理的缓存分区中
 **/
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "edu.hubu.mall.product.feign")
public class MallProductApplication {

    public static void main(String[] args) {
        SpringApplication.run(MallProductApplication.class,args);
    }

}
