package edu.hubu.mall.cart.controller;

import edu.hubu.mall.cart.interceptor.CartLoginInterceptor;
import edu.hubu.mall.cart.service.CartService;
import edu.hubu.mall.cart.vo.Cart;
import edu.hubu.mall.cart.vo.CartItem;
import edu.hubu.mall.common.auth.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.concurrent.ExecutionException;

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
    public String cartPage(Model model) throws ExecutionException, InterruptedException {
        Cart cart = cartService.getMyCart();
        model.addAttribute("cart",cart);
        return "cartList";
    }

    /**
     * 添加商品到购物车,添加成功后跳转到添加添加成功页面
     * 防止重复添加：重定向到添加成功的界面,通过redirectAttributes将skuId带到重定向的页面
     */
    @GetMapping("/addToCart")
    public String addToCart(@RequestParam("skuId") Long skuId, @RequestParam("num") Integer num, RedirectAttributes ra)
            throws ExecutionException, InterruptedException {
        cartService.addToCart(skuId,num);
        ra.addAttribute("skuId",skuId);
        return "redirect:http://cart.emall.com/addToCartSuccess.html";
    }


    /**
     * 添加商品成功跳转到添加商品成功页面
     */
    @GetMapping("addToCartSuccess.html")
    public String addCartSuccess(@RequestParam("skuId") Long skuId, Model model){
        CartItem cartItem = cartService.getCartItem(skuId);
        model.addAttribute("cartItem",cartItem);
        return "success";
    }

    /**
     * 设置购物项的选中与取消
     */
    @GetMapping("/checkItem")
    public String checkCartItem(@RequestParam("skuId") Long skuId,@RequestParam("checked") Integer checked){
        cartService.checkCartItem(skuId,checked);
        return "redirect:http://cart.emall.com/cart.html";
    }

    /**
     * 修改购物项商品的数量
     */
    @GetMapping("/countItem")
    public String changeItemCount(@RequestParam("skuId") Long skuId,@RequestParam("num") Integer num){
        cartService.changeItemCount(skuId,num);
        return "redirect:http://cart.emall.com/cart.html";
    }

    /**
     * 删除购物的商品
     */
    @GetMapping("/deleteItem")
    public String deleteCartItem(@RequestParam("skuId") Long skuId){
        cartService.deleteItem(skuId);
        return "redirect:http://cart.emall.com/cart.html";
    }

}
