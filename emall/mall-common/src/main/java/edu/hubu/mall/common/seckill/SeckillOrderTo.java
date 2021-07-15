package edu.hubu.mall.common.seckill;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 秒杀成功生成订单
 * @Author: huxiaoge
 * @Date: 2021-07-15
 **/
@Data
public class SeckillOrderTo {

    /**
     * 订单号
     */
    private String orderSn;

    /**
     * 活动场次id
     */
    private Long promotionSessionId;
    /**
     * 商品id
     */
    private Long skuId;
    /**
     * 秒杀价格
     */
    private BigDecimal seckillPrice;

    /**
     * 购买数量
     */
    private Integer num;

    /**
     * 会员ID
     */
    private Long memberId;

}
