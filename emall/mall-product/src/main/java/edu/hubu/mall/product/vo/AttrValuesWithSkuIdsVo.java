package edu.hubu.mall.product.vo;

import lombok.Data;

/**
 * @Description: 封装sku的每一个attrValue包含哪些sku(skuIds)
 * @Author: huxiaoge
 * @Date: 2021-05-20
 **/
@Data
public class AttrValuesWithSkuIdsVo {

    private String attrValue;
    /**
     * skuIds的聚合结果
     */
    private String skuIds;
}
