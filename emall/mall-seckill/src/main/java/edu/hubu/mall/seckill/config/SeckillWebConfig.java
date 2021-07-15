package edu.hubu.mall.seckill.config;

import edu.hubu.mall.seckill.interceptor.LoginRequireInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-07-15
 **/
@Configuration
public class SeckillWebConfig  implements WebMvcConfigurer {

    @Autowired
    LoginRequireInterceptor loginRequireInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginRequireInterceptor).addPathPatterns("/**");
    }
}
