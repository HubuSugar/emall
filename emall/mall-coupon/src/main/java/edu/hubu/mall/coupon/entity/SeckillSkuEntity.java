package edu.hubu.mall.coupon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description: 秒杀场次与商品关联实体
 * @Author: huxiaoge
 * @Date: 2021-07-09
 **/
@Data
@TableName("sms_seckill_sku_relation")
public class SeckillSkuEntity {

    @TableId(type = IdType.AUTO)
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

}
