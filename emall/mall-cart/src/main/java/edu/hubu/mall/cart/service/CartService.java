package edu.hubu.mall.cart.service;

import edu.hubu.mall.cart.vo.Cart;
import edu.hubu.mall.cart.vo.CartItem;
import edu.hubu.mall.common.order.OrderItemVo;

import java.util.List;
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

    /**
     * 切换购物车中购物项的选中状态
     * @param skuId
     * @param check
     */
    void checkCartItem(Long skuId, Integer check);

    /**
     * 改变商品的数量
     * @param skuId
     * @param num
     */
    void changeItemCount(Long skuId, Integer num);

    /**
     * 删除购物项
     * @param skuId
     */
    void deleteItem(Long skuId);

    /**
     * 查询用户订单结算页的购物项信息
     * @return
     */
    List<OrderItemVo> queryMemberCartItems();
}
