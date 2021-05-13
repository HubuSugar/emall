package edu.hubu.mall.search.constant;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
public class ElasticConstant {

    /**
     * 保存在es中的商品索引
     */
    public static final String PRODUCT_ES_INDEX = "product";
    /**
     * 保存在es中的商品类型
     */
    public static final String PRODUCT_ES_TYPE = "skuInfo";

    /**
     * 查询es时的每页大小
     */
    public static final Integer PRODUCT_ES_PAGESIZE = 16;
}
