package edu.hubu.mall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/26
 * @Description: es配置
 **/
@Configuration
public class MallElasticConfig {

    @Value("${elastic.host}")
    private String esHost;

    @Value("${elastic.port}")
    private Integer esPort;

    public static final RequestOptions COMMON_OPTIONS;

    /**
     * 初始化es请求的基本配置
     */
    static {
        RequestOptions.Builder builder = RequestOptions.DEFAULT.toBuilder();

        COMMON_OPTIONS = builder.build();
    }

    /**
     * RestClient:es官方提供的使用操作es的client
     * @return 向spring容器中注入es操作客户端
     */
    @Bean
    public RestHighLevelClient restHighLevelClient() {
        HttpHost httpHost = new HttpHost(esHost, esPort,"http");
        Node node = new Node(httpHost);
        RestClientBuilder restClientBuilder = RestClient.builder(node);
        return new RestHighLevelClient(restClientBuilder);
    }


}
