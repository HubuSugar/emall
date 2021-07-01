package edu.hubu.mall.member.config;

import edu.hubu.mall.member.interceptor.LoginRequireInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-04
 **/
@Configuration
public class MemberWebConfig implements WebMvcConfigurer {

    @Autowired
    private LoginRequireInterceptor loginRequireInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginRequireInterceptor).addPathPatterns("/**");
    }
}
