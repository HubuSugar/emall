package edu.hubu.mall.cart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-27
 **/
@Controller
public class CartController {

    @GetMapping("/cart.html")
    public String cartPage(){
        return "cartList";
    }
}
