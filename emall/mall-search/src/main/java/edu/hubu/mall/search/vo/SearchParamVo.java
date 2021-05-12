package edu.hubu.mall.search.vo;

import lombok.Data;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021-05-12
 * @Description: 封装商品搜索服务前端传过来的参数
 * catalog3Id=225&keyword=小米
 **/
@Data
public class SearchParamVo {

    /**
     * 搜索关键字 skuTitle
     */
    private String keyword;

    /**
     * 商品服务首页传递的商品分类id
     */
    private String catalog3Id;

    /**
     * 仅显示有货(0 -- 无货；1--有货)
     */
    private Integer hasStock;

    /**
     * sku价格区间查询条件
     * skuPrice=1_500/500_/_500
     */
    private String skuPrice;

    /**
     * 品牌查询
     * 支持多选：多个品牌
     * brandId=1
     */
    private List<Long> brandIds;

    /**
     * 属性查询
     * attrs=1_其他:安卓&attrs=5_2G:4G
     */
    private List<String> attrs;

    /**
     * 排序条件（saleCount -- 销量；hotScore -- 热度分； skuPrice -- 商品的价格）,只能选择一种排序
     * 格式：sort=saleCount_desc
     *      sort=skuPrice_desc
     *      sort=hotScore_desc
     */
    private String sort;

    /**
     * 分页属性
     */
    private Integer pageNum;

}
