package edu.hubu.mall.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: huxiaoge
 * @Date: 2021-07-12
 * @Description: redissonClient客户端的配置（分布式锁）
 **/
@Configuration
public class RedissonClientConfig {

    @Bean(destroyMethod = "shutdown")
    RedissonClient redissonConfig(){
        //创建配置
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:6379");
        //根据config创建redisson客户端的对象
        return Redisson.create(config);
    }

}
