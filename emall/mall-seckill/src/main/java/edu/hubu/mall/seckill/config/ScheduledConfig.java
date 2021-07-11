package edu.hubu.mall.seckill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @Description: 定时任务的配置
 *              @EnableScheduling:  开启定时任务的自动配置
 *              @EnableAsync： 开启任务的异步执行，保证定时任务不阻塞
 * @Author: huxiaoge
 * @Date: 2021-07-11
 **/
@Configuration
@EnableScheduling
@EnableAsync
public class ScheduledConfig {
}
