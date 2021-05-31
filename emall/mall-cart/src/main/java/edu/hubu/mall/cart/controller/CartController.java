package edu.hubu.mall.cart.controller;

import edu.hubu.mall.cart.interceptor.CartLoginInterceptor;
import edu.hubu.mall.cart.service.CartService;
import edu.hubu.mall.cart.vo.CartItem;
import edu.hubu.mall.common.auth.HostHolder;
import edu.hubu.mall.common.constant.AuthConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-27
 **/
@Controller
public class CartController {

    @Autowired
    CartService cartService;

    /**
     * 浏览器有一个表示临时用户身份的user-key
     * @return
     */
    @GetMapping("/cart.html")
    public String cartPage(){
        HostHolder hostHolder = CartLoginInterceptor.threadLocal.get();


        System.out.println(hostHolder);
        return "cartList";
    }

    /**
     * 添加商品到购物车
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, Model model){
        CartItem cart = cartService.addToCart(skuId,num);
        model.addAttribute("cartItem",cart);
        return "success";
    }
}
