package edu.hubu.mall.common.product;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-01
 **/
@Data
public class SkuInfoVo {

    private Long skuId;

    private Long spuId;

    /**
     * sku名称
     */
    private String skuName;

    /**
     * sku介绍描述
     */
    private String skuDesc;

    /**
     * 分类Id
     */
    private Long catalogId;

    /**
     * 品牌Id
     */
    private Long brandId;

    /**
     * sku默认图片
     */
    private String skuDefaultImg;

    /**
     * sku标题
     */
    private String skuTitle;

    /**
     * 副标题
     */
    private String skuSubtitle;

    /**
     * sku价格
     */
    private BigDecimal price;

    /**
     * sku销量
     */
    private Long saleCount;
}
