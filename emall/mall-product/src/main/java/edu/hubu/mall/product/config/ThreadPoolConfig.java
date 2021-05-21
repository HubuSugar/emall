package edu.hubu.mall.product.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @Description: 自定义线程池配置
 * @Author: huxiaoge
 * @Date: 2021-05-20
 *
 * 两种自定义配置参数的方式
 *  需要可以在yml文件直接配置需要在配置类上，如ThreadPoolProperties 上加 @ConfigurationProperties注解
 *  1.在要注入容器中的配置内上加 @EnableConfigurationProperties(配置类.class) ，注意配置类上不能加Component注解
 *   1.1 要保证配置类的属性有默认值
 *   1.2 @EnableConfigurationProperties 相当于将配置类帮我们注入到容器中
 *  2.在配置类上加 @Component、@Configuration注解, 然后当前类不用@EnableConfigurationProperties注解
 **/
@Configuration
@EnableConfigurationProperties(ThreadPoolProperties.class)
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor threadPoolBuild(ThreadPoolProperties poolProperties){
        return new ThreadPoolExecutor(
                poolProperties.getCoreSize(),
                poolProperties.getMaxSize(),
                poolProperties.getKeepAliveTime(),
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100000),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

}
