package edu.hubu.mall.webController;

import cn.hutool.core.util.IdUtil;
import org.redisson.Redisson;
import org.redisson.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @Author: huxiaoge
 * @Date: 2021-05-07
 * @Description:
 **/
@Controller
public class JucTestController {

    @Autowired
    private RedissonClient redisson;

    @Autowired
    private StringRedisTemplate redisTemplate;

    /**
     * 测试接口
     */
    @GetMapping("/hello")
    @ResponseBody
    public String hello(){

        //1.获取一把锁，只要锁的名字一样，就表示是同一把锁
        RLock lock = redisson.getLock("my-lock");

        //2.加锁
        lock.lock();    //阻塞式等待，默认加锁时间是30s,锁的自动续期

        try {
            System.out.println("加锁成功...执行业务" + Thread.currentThread().getId());
            Thread.sleep(30000);
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            System.out.println("释放锁" + Thread.currentThread().getId());
            lock.unlock();
        }
        return "hello";
    }

    /**
     * 改数据加写锁，读数据加读锁
     * 读写锁测试
     * 读 + 读：无锁状态
     * 读 + 写：读完之后再写入新的内容
     * 写 + 写：依次读
     * 写 + 读：写业务完成后读写入的内容
     */
    @GetMapping("/write")
    @ResponseBody
    public String writeLock(){
        RReadWriteLock rwLock = redisson.getReadWriteLock("rw-lock");
        RLock rLock = rwLock.writeLock();
        String s = "";
        try {
            rLock.lock();
            System.out.println("写锁加锁成功..." + Thread.currentThread().getId());
            s = IdUtil.simpleUUID();
            redisTemplate.opsForValue().set("writeValue",s);
            Thread.sleep(30000);
        }catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
            System.out.println("写锁释放..." + Thread.currentThread().getId());

        }
        return s;
    }

    @GetMapping("/read")
    @ResponseBody
    public String readLock() {
        RReadWriteLock rwLock = redisson.getReadWriteLock("rw-lock");
        RLock rLock = rwLock.readLock();
        String s = "";
        try {
            rLock.lock();
            System.out.println("读锁加锁成功..." + Thread.currentThread().getId());
            s = redisTemplate.opsForValue().get("writeValue");
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            rLock.unlock();
            System.out.println("读锁释放..." + Thread.currentThread().getId());
        }
        return s;
    }

    /**
     * 闭锁 CountDownLatch
     * 所有线程都做完了一个操作，才释放锁
     * 例子：放假时，每个班的人都走完了，才锁门
     */
    @GetMapping("/lockDoor")
    @ResponseBody
    public String lockDoor(){
        RCountDownLatch cdLock = redisson.getCountDownLatch("cd-lock");
        cdLock.trySetCount(5);
        try {
            cdLock.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "放假了...";
    }

    @GetMapping("/gogo/{id}")
    @ResponseBody
    public String goOut(@PathVariable("id") String id){
        RCountDownLatch cdLock = redisson.getCountDownLatch("cd-lock");
        cdLock.countDown();
        return id + "班的人都走了";
    }

    /**
     * 信号量测试
     */
    @GetMapping("/park")
    @ResponseBody
    public String park() throws InterruptedException {
        RSemaphore semaphore = redisson.getSemaphore("park-lock");
        semaphore.acquire();
        boolean b = semaphore.tryAcquire(10, TimeUnit.SECONDS);
        if(b){
            return "停车成功,剩余车位：" + semaphore.availablePermits();
        }else{
            return "没有车位了";
        }

    }

    @GetMapping("/driver")
    @ResponseBody
    public String goOut(){
        RSemaphore semaphore = redisson.getSemaphore("park-lock");
        semaphore.release();
        return "开走一辆车，剩余车位：" + semaphore.availablePermits();
    }


}
