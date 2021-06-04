package edu.hubu.mall.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @Description: 自定义线程池的配置
 * @Author: huxiaoge
 * @Date: 2021-05-20
 **/
@ConfigurationProperties(prefix = "spring.mythread")
//@Component
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
