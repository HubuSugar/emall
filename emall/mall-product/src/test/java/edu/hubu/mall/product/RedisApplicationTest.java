package edu.hubu.mall.product;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import edu.hubu.mall.MallProductApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Author: huxiaoge
 * @Date: 2021-04-29
 * @Description:
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisApplicationTest {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    public void redisOps(){
        redisTemplate.opsForValue().set("hello","world" + IdUtil.simpleUUID());
        System.out.println("hello对应的值{}" + redisTemplate.opsForValue().get("hello"));
    }
}
