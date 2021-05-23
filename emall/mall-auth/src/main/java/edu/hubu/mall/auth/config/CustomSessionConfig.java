package edu.hubu.mall.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * @Author: huxiaoge
 * @Date: 2021/5/24
 * @Description: 自定义session的配置
 **/
@Configuration
public class CustomSessionConfig {

    /**
     * cookie设置域名
     */
    @Bean
    public CookieSerializer cookieSerializer(){
        DefaultCookieSerializer cookie = new DefaultCookieSerializer();
        cookie.setCookieName("ESESSIONID");
        cookie.setDomainName("emall.com");

        return cookie;
    }


    /**
     * redis的序列化器
     * @return
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }
}
