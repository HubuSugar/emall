package edu.hubu.mall.cart.service;

import edu.hubu.mall.cart.vo.Cart;
import edu.hubu.mall.cart.vo.CartItem;

import java.util.concurrent.ExecutionException;

/**
 * @Author: huxiaoge
 * @Date: 2021/5/31
 * @Description:
 **/
public interface CartService {

    /**
     * 添加购物车
     * @param skuId
     * @param num
     * @return
     */
    CartItem addToCart(Long skuId, Integer num) throws ExecutionException, InterruptedException;

    /**
     * 查询购物车数据
     * @return
     */
    Cart getMyCart() throws ExecutionException, InterruptedException;

    /**
     * 根据key获取购物车中某一个购物项的信息
     * @param skuId
     * @return
     */
    CartItem getCartItem(Long skuId);

    /**
     * 清空购物车的数据
     * @param cartKey
     */
    public void clearCartInfo(String cartKey);
}
