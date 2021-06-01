package edu.hubu.mall.cart.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description: 线程池配置
 * @Author: huxiaoge
 * @Date: 2021-06-01
 **/
@ConfigurationProperties(prefix = "spring.mythread")
@Data
public class ThreadPoolProperties {
    /**
     * 核心线程数
     */
    private Integer coreSize;
    /**
     * 最大线程数
     */
    private Integer maxSize;
    /**
     * 核心线程空闲时间
     */
    private Integer keepAliveTime;
}
