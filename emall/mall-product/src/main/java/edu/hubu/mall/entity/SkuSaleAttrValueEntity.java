package edu.hubu.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-19
 **/
@Data
@TableName("pms_sku_sale_attr_value")
public class SkuSaleAttrValueEntity {

    /**
     * 主键
     */
    @TableId(type= IdType.AUTO)
    private Long id;

    /**
     * skuId
     */
    private Long skuId;

    /**
     * 属性id
     */
    private Long attrId;

    private String attrName;

    private String attrValue;

    private Integer attrSort;

}
