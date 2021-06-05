package edu.hubu.mall.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/5
 * @Description: 订单中的购物项信息
 **/
@Data
@TableName("oms_order_item")
public class OrderItemEntity {

    private Long id;
    private Long orderId;   //订单id
    private String orderSn;  //订单号
    private Long spuId;
    private String spuName;
    private String spuPic;
    private String spuBrand;
    private Long categoryId;
    private Long skuId;
    private String skuName;
    private String skuPic;
    private BigDecimal skuPrice;
    private Integer skuQuantity;   //sku购买数量
    private String skuAttrsVals;   //sku销售属性
    private BigDecimal promotionAmount;   //促销优惠金额
    private BigDecimal couponAmount;   //优惠券优惠金额
    private BigDecimal integrationAmount;   //积分优惠金额
    private BigDecimal realAmount;   //优惠后的实际金额
    private Integer giftIntegration;  //赠送积分
    private Integer giftGrowth;     //赠送成长值

}
