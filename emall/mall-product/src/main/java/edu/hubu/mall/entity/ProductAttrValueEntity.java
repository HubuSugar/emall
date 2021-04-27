package edu.hubu.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description: spu对应的商品属性实体
 **/
@Data
@TableName(value = "pms_product_attr_value")
public class ProductAttrValueEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long spuId;

    private Long attrId;

    /**
     * 属性名称
     */
    private String attrName;

    /**
     * 属性值
     */
    private String attrValue;

    /**
     * 属性排序
     */
    private Integer attrSort;

    /**
     * 是否展示在介绍上 0-否；1-是
     */
    private Integer quickShow;
}
