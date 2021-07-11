package edu.hubu.mall.seckill.scheduled;

import edu.hubu.mall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

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
@Slf4j
@Service
public class SeckillSkuScheduled {

    @Autowired
    SeckillService seckillService;

    @Async
    @Scheduled(cron = "* * 3 * * ?")
    public void uploadSeckillSkuLatest3Days(){

        try{
           seckillService.uploadSeckillSkuLatest3Days();
        }catch (Exception e){

        }finally {

        }
    }



}
