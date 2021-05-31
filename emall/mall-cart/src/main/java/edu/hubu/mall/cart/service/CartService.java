package edu.hubu.mall.cart.service;

import edu.hubu.mall.cart.vo.CartItem;

/**
 * @Author: huxiaoge
 * @Date: 2021/5/31
 * @Description:
 **/
public interface CartService {

    CartItem addToCart(Long skuId, Integer num);
}
