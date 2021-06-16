package edu.hubu.mall.common.constant;

/**
 * @Author: huxiaoge
 * @Date: 2021-06-15
 * @Description:
 **/
public class WareConstant {

    /**
     * 存库服务的交换机
     */
    public static final String WARE_EVENT_EXCHANGE = "ware-event-exchange";

    /**
     * 库存服务的死信队列（延迟队列）
     */
    public static final String WARE_DELAY_QUEUE = "ware.stock.delay.queue";

    /**
     * 路由
     */
    public static final String WARE_STOCK_LOCKED_ROUTE = "stock.locked";

    /**
     * 库存释放队列
     */
    public static final String WARE_RELEASE_QUEUE = "stock.release.queue";

    /**
     * 库存释放队列
     */
    public static final String WARE_STOCK_RELEASE_ROUTE = "stock.release";

    /**
     * 订单从死信队列释放的超时时间,单位ms(2分钟)
     */
    public static final Integer WARE_RELEASE_TIMEOUT = 1000 * 120;

}
