package edu.hubu.mall.common.constant;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/5
 * @Description: 订单服务用到的常量
 **/
public class OrderConstant {

    /**
     * 订单令牌前缀
     */
    public static final String ORDER_TOKEN_PREFIX = "order:token:";

    /**
     * 防重令牌过期时间
     */
    public static final long ORDER_TOKEN_TIMEOUT = 30;

    /**
     * 订单自动确认收货天数
     */
    public static final Integer ORDER_AUTO_CONFIRM_DAYS = 7;

    /**
     * 订单验价的误差范围
     */
    public static final double ORDER_PRICE_ALLOW_GAP = 0.01;
}
