package edu.hubu.mall.order.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/5
 * @Description: 封装订单提交的数据
 **/
@Data
public class OrderSubmitTo {

    /**
     * 收获地址
     */
    private Long addrId;

    /**
     * 付款方式
     */
    private Integer payType;

    // 省略优惠，物流等信息

    /**
     * 防重令牌
     */
    private String orderToken;

    /**
     * 订单应付价格(用于订单验价)
     */
    private BigDecimal payPrice;

    /**
     * 无需提交需要购买的商品信息，提交后去购物车重新查询，
     * 用户信息也不用提交，直接到session中获得
     */

    /**
     * 订单备注信息
     */
    private String note;

}
