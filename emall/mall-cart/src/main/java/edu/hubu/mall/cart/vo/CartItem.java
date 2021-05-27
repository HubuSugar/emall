package edu.hubu.mall.cart.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Description: 购物车的每个购物项信息
 * @Author: huxiaoge
 * @Date: 2021-05-27
 **/
public class CartItem {

    private Long skuId;
    private boolean check = true;
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
     * 总价
     */
    private BigDecimal totalPrice;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<String> getSkuAttr() {
        return skuAttr;
    }

    public void setSkuAttr(List<String> skuAttr) {
        this.skuAttr = skuAttr;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public BigDecimal getTotalPrice() {
        return this.price.multiply(new BigDecimal(count));
    }

    /**
     * 自动计算每个购物项的价格
     * @param totalPrice
     */
    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }
}
