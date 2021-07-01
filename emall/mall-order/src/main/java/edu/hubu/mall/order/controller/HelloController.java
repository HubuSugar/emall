package edu.hubu.mall.order.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-07-01
 **/
@RestController
public class HelloController {

    @GetMapping("/test")
    public String test(){
        return "success";
    }
}
