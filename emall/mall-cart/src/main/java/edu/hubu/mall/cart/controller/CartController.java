package edu.hubu.mall.cart.controller;

import edu.hubu.mall.cart.interceptor.CartLoginInterceptor;
import edu.hubu.mall.common.auth.HostHolder;
import edu.hubu.mall.common.constant.AuthConstant;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-27
 **/
@Controller
public class CartController {

    /**
     * 浏览器有一个表示临时用户身份的user-key
     * @return
     */
    @GetMapping("/cart.html")
    public String cartPage(){
        HostHolder hostHolder = CartLoginInterceptor.threadLocal.get();

        return "cartList";
    }
}
