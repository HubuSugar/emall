package edu.hubu.mall.order.to;

import edu.hubu.mall.order.entity.OrderEntity;
import edu.hubu.mall.order.entity.OrderItemEntity;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/5
 * @Description: 封装订单创建方法返回的数据
 **/
@Data
public class OrderCreatedTo {

    /**
     * 订单数据
     */
    private OrderEntity order;
    /**
     * 订单项的数据
     */
    private List<OrderItemEntity> orderItems;

    /**
     * 订单应付价格
     */
    private BigDecimal payPrice;

    /**
     * 订单运费
     */
    private BigDecimal fare;

}
