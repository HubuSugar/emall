package edu.hubu.mall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/23
 * @Description: 在网关统一处理跨域请求
 **/
@Configuration
public class CorsConfig {

    @Bean
    public CorsWebFilter buildCorsWebFilter(){
        UrlBasedCorsConfigurationSource configurationSource = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        //允许访问的请求头类型
        corsConfiguration.addAllowedHeader("*");
        //允许的请求方法类型
        corsConfiguration.setAllowedMethods(Arrays.asList("GET","POST","PUT","DELETE","OPTIONS"));
        //允许访问的请求来源
        corsConfiguration.addAllowedOrigin("*");
        //是否允许携带cookie的请求跨域
        corsConfiguration.setAllowCredentials(true);
        //注册跨域配置项
        configurationSource.registerCorsConfiguration("/**",corsConfiguration);

        return new CorsWebFilter(configurationSource);
    }

}
