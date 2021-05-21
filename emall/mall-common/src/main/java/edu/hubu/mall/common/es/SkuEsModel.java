package edu.hubu.mall.common.es;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/26
 * @Description: 商品上架到es的数据模型
 **/
@Data
public class SkuEsModel {

    private Long skuId;

    private Long spuId;

    private String skuTitle;

    private BigDecimal skuPrice;

    private String skuImg;

    private Long saleCount;

    /**
     * sku是否有存库
     */
    private Boolean hasStock;

    /**
     * 热度评分
     */
    private Long hotScore;

    private Long brandId;

    private String brandName;

    private String brandImg;

    private Long catalogId;

    private String catalogName;

    private List<Attrs> attrs;

    /**
     * 检索属性
     */
    @Data
    public static class Attrs{
        private Long attrId;
        private String attrName;
        private String attrValue;
    }

}
