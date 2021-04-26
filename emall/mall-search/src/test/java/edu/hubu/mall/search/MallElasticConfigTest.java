package edu.hubu.mall.search;

import org.elasticsearch.client.RestHighLevelClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.security.RunAs;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/26
 * @Description: elastic测试用例
 **/
@RunWith(SpringRunner.class)
@SpringBootTest
public class MallElasticConfigTest {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @Test
    public void printClient(){
        System.out.println(restHighLevelClient);
    }
}
