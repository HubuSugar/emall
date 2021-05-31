package edu.hubu.mall.cart.vo;

import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 购物车模型对象
 * @Author: huxiaoge
 * @Date: 2021-05-27
 **/
public class Cart {
    /**
     * 每个购物项
     */
    private List<CartItem> items;

    /**
     * 商品的总数量
     */
    private Integer countNum;

    /**
     * 商品的种类
     */
    private Integer countType;

    /**
     * 整个购物车总价格
     */
    private BigDecimal totalAmount;

    /**
     * 优惠价格
     */
    private BigDecimal reduce = new BigDecimal("0.00");

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    /**
     * 累加每一个购物项的商品件数
     * @return
     */
    public Integer getCountNum() {
        Integer count = 0;
        if(!CollectionUtils.isEmpty(this.items)){
            for (CartItem item:items) {
                count += item.getCount();
            }
        }
        return count;
    }

    /**
     * 商品的种类
     * @return
     */
    public Integer getCountType() {
        if(CollectionUtils.isEmpty(items)){
            return 0;
        }
        return items.size();
    }

    /**
     * 购物车的总价
     * @return
     */
    public BigDecimal getTotalAmount() {
        BigDecimal amount = new BigDecimal("0.00");
        if(!CollectionUtils.isEmpty(items)){
            for (CartItem item: items) {
                BigDecimal totalPrice = item.getTotalPrice();
                amount = amount.add(totalPrice);
            }
        }
        //减去优惠的价格
        return amount.subtract(reduce);
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getReduce() {
        return reduce;
    }

    public void setReduce(BigDecimal reduce) {
        this.reduce = reduce;
    }
}
