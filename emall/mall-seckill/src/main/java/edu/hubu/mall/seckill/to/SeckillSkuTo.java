package edu.hubu.mall.seckill.to;

import edu.hubu.mall.common.product.SkuInfoVo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: huxiaoge
 * @Date: 2021-07-12
 * @Description: 保存进redis的秒杀对象
 * 包含以下信息：
 *      商品的秒杀信息
 *      商品的基本信息
 *      商品的秒杀随机码
 **/
@Data
public class SeckillSkuTo {

    private Long id;

    private Long promotionId;

    private Long promotionSessionId;

    private Long skuId;

    private BigDecimal seckillPrice;

    private Integer seckillCount;

    private Integer seckillLimit;

    private Integer seckillSort;

    /**
     * 商品的详细信息
     */
    private SkuInfoVo skuInfo;
    /**
     * 商品的秒杀开始时间
     */
    private Long startTime;
    /**
     * 商品的秒杀结束时间
     */
    private Long endTime;
    /**
     * 商品的秒杀随机码,到了秒杀时间才生成，保证秒杀商品的接口被刷
     */
    private String randomCode;

}
