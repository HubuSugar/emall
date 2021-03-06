package edu.hubu.mall.product.vo;

import lombok.Data;

import java.util.List;

/**
 * @Description: 每个sku的销售属性(表示每个属性下的所有属性值)
 * @Author: huxiaoge
 * @Date: 2021-05-19
 **/
@Data
public class SkuItemSaleAttrVo {

    private Long attrId;
    private String attrName;
    /**
     *属性的聚合值
     */
    private List<AttrValuesWithSkuIdsVo> attrValues;

}
