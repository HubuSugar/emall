package edu.hubu.mall.common.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 订单结算的购物项信息
 * @Author: huxiaoge
 * @Date: 2021-06-04
 **/
@AllArgsConstructor
@NoArgsConstructor
@Data
public class OrderItemVo {

    private Long skuId;
    /**
     * 商品的标题
     */
    private String title;
    /**
     * 图片
     */
    private String image;

    /**
     * 选中的商品属性
     */
    private List<String> skuAttr;

    /**
     * 单价
     */
    private BigDecimal price;
    /**
     * 购买的数量
     */
    private Integer count;

    /**
     * 商品的重量
     */
    private BigDecimal weight = new BigDecimal("0.75");
}
