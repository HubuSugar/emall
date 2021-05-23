package edu.hubu.mall.auth.config;

import lombok.extern.apachecommons.CommonsLog;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * @Author: huxiaoge
 * @Date: 2021/5/22
 * @Description:
 **/
@Component
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplateBuild(){

        SimpleClientHttpRequestFactory simpleRequestFactory = new SimpleClientHttpRequestFactory();
        simpleRequestFactory.setReadTimeout(10000);
        simpleRequestFactory.setConnectTimeout(10000);

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(simpleRequestFactory);
        return restTemplate;
    }
}
