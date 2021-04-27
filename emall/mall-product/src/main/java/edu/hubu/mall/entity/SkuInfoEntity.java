package edu.hubu.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description: sku_info信息实体
 **/
@Data
@TableName("pms_sku_info")
public class SkuInfoEntity implements Serializable {

    @TableId(type = IdType.AUTO)
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
