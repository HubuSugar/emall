package edu.hubu.mall.cart.config;

import edu.hubu.mall.cart.interceptor.CartLoginInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @Description: 注册用户身份的拦截器
 * @Author: huxiaoge
 * @Date: 2021-05-31
 **/
@Configuration
public class CartMvcConfig implements WebMvcConfigurer {

    @Autowired
    private CartLoginInterceptor loginInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor).addPathPatterns("/**");
    }
}
