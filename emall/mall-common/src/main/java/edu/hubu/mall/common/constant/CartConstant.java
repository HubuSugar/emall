package edu.hubu.mall.common.constant;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-31
 **/
public class CartConstant {
    public static final String TEMP_USER_COOKIE_NAME = "user-key";
    //临时用户的cookie过期时间
    public static final Integer TEMP_USER_COOKIE_TIMEOUT = 30 * 24 * 60 * 60;

    //用户购物车的redis前缀
    public static final String CART_PREFIX = "emall:cart:";
}
