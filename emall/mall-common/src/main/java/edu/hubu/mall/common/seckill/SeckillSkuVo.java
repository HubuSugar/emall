package edu.hubu.mall.common.seckill;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-07-11
 **/
@Data
public class SeckillSkuVo {

    private Long id;

    private Long promotionId;

    /**
     * 活动场次id
     */
    private Long promotionSessionId;

    /**
     * 商品id
     */
    private Long skuId;

    /**
     * 价格
     */
    private BigDecimal seckillPrice;
    /**
     * 总件数
     */
    private Integer seckillCount;
    /**
     * 限购件数
     */
    private Integer seckillLimit;
    /**
     * 排序
     */
    private Integer seckillSort;

    //当前商品秒杀的开始时间
    private Long startTime;

    //当前商品秒杀的结束时间
    private Long endTime;

    //当前商品秒杀的随机码
    private String randomCode;
}
