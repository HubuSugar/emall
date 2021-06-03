package edu.hubu.mall.order.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/3
 * @Description:
 **/
@Controller
public class OrderController {


    @GetMapping("/detail")
    public String orderList(){

        return "detail";
    }

    @GetMapping("/toTrade")
    public String tradeConfirm(){
        return "confirm";
    }
}
