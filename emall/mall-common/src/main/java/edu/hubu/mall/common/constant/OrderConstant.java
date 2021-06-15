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

    /**
     * 订单业务的交换机（topic）
     */
    public static final String ORDER_EVENT_EXCHANGE = "order-event-exchange";

    /**
     * 订单的死信队列
     */
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";

    /**
     * 订单发送到死信队列用到的路由
     */
    public static final String ORDER_CREATE_ROUTE = "order.create.order";

    /**
     * 死信订单释放后的队列
     */
    public static final String ORDER_RELEASE_QUEUE = "order.release.order.queue";

    /**
     * 订单释放用到的路由
     */
    public static final String ORDER_RELEASE_ROUTE = "order.release.order";

    /**
     * 订单从死信队列释放的超时时间,单位ms
     */
    public static final Integer ORDER_RELEASE_TIMEOUT = 1000 * 60;
}
