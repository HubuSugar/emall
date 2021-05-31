package edu.hubu.mall.cart.service.impl;

import edu.hubu.mall.cart.interceptor.CartLoginInterceptor;
import edu.hubu.mall.cart.service.CartService;
import edu.hubu.mall.cart.vo.CartItem;
import edu.hubu.mall.common.auth.HostHolder;
import edu.hubu.mall.common.constant.CartConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @Author: huxiaoge
 * @Date: 2021/5/31
 * @Description:
 **/
@Service
public class CartServiceImpl implements CartService {

    @Autowired
    StringRedisTemplate redisTemplate;

    /**
     * 添加商品到购物车
     * @param skuId 商品id
     * @param num 商品件数
     * @return
     */
    @Override
    public CartItem addToCart(Long skuId, Integer num) {
        BoundHashOperations<String, Object, Object> opsCart = getOpsCart();
        return null;
    }


    /**
     * 获取要操作的用户的购物车
     * @return
     */
    private BoundHashOperations<String, Object, Object> getOpsCart(){
        HostHolder hostHolder = CartLoginInterceptor.threadLocal.get();
        String cartKey = CartConstant.CART_PREFIX;
        if(hostHolder.getUserId() != null){
            cartKey = cartKey + hostHolder.getUserId();
        }else{
            cartKey += hostHolder.getUserKey();
        }
        return redisTemplate.boundHashOps(cartKey);
    }
}
