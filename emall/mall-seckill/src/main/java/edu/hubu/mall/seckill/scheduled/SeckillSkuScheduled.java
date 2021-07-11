package edu.hubu.mall.seckill.scheduled;

import org.springframework.stereotype.Component;

/**
 * @Description: 上架秒杀商品的定时任务
 * @Author: huxiaoge
 * @Date: 2021-07-11
 *
 * 实现定时任务不阻塞：
 *      1、提交到自定义异步任务线程池、CompatableFuture + executor
 *      2、使用spring boot的定时任务
 *        @EnableScheduling (使用taskSchedulingProperties自动配置)、还是存在任务阻塞的bug
 *      3、使用spring的异步任务
 *        @EnableAsync 开启异步任务功能
 *        @Async 需要异步执行的方法
 **/
@Component
public class SeckillSkuScheduled {




}
