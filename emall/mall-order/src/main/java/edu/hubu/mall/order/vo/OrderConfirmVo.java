package edu.hubu.mall.order.vo;

import edu.hubu.mall.common.order.OrderItemVo;
import edu.hubu.mall.common.member.MemberReceiveAddressVo;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * @Description: 订单确认页需要的数据
 * @Author: huxiaoge
 * @Date: 2021-06-04
 **/
public class OrderConfirmVo {

    /**
     * 用户的收货地址信息
     */
    @Setter @Getter
    private List<MemberReceiveAddressVo> address;

    /**
     * 所有选中的购物项
     */
    @Setter @Getter
    private List<OrderItemVo> items;

    //发票记录...(略)
    /**
     * 优惠券信息
     * 积分
     */
    @Setter @Getter
    private Integer integration;

    private Integer count;

    /**
     * 订单总额
     */
    private BigDecimal totalPrice;

    //优惠总额(略)

    /**
     * 商品应付总额
     */
    private BigDecimal payPrice;

    /**
     * 订单防重令牌
     */
    @Setter @Getter
    private String orderToken;

    /**
     * 记录每个sku是否有库存
     */
    @Setter @Getter
    private Map<Long,Boolean> stocks;


    public Integer getCount() {
        Integer count = 0;
        if(CollectionUtils.isEmpty(items)){
            return count;
        }
        for (OrderItemVo item:items) {
            count += item.getCount();
        };
        return count;
    }

    /**
     * 计算订单总额
     * @return
     */
    public BigDecimal getTotalPrice() {
        BigDecimal sum = new BigDecimal("0");
        if(items != null){
            for(OrderItemVo item:items){
                BigDecimal multiply = item.getPrice().multiply(new BigDecimal(item.getCount()));
                sum = multiply.add(sum);
            }
        }
        return sum;
    }

    /**
     * 应付总额
     * @return
     */
    public BigDecimal getPayPrice() {
        return getTotalPrice();
    }
}
