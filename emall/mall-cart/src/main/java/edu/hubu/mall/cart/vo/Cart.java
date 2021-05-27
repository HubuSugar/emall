package edu.hubu.mall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 购物车模型对象
 * @Author: huxiaoge
 * @Date: 2021-05-27
 **/
@Data
public class Cart {
    /**
     * 每个购物项
     */
    private List<CartItem> items;

    /**
     * 总价格
     */
    private BigDecimal totalPrice;

}
