package edu.hubu.mall.product;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.IdUtil;
import edu.hubu.mall.MallProductApplication;
import edu.hubu.mall.service.AttrGroupService;
import edu.hubu.mall.vo.SpuItemAttrGroupVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

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

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private AttrGroupService attrGroupService;

    @Test
    public void redisOps(){
        redisTemplate.opsForValue().set("hello","world" + IdUtil.simpleUUID());
        System.out.println("hello对应的值{}" + redisTemplate.opsForValue().get("hello"));
    }

    @Test
    public void attrgroup(){
        List<SpuItemAttrGroupVo> attrGroupWithAttrsBySpuId = attrGroupService.getAttrGroupWithAttrsBySpuId(13L, 225L);
        System.out.println(attrGroupWithAttrsBySpuId);
    }

    @Test
    public void redissonCli(){
        System.out.println(redissonClient);
    }
}
