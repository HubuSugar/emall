package edu.hubu.mall.search.vo;

import edu.hubu.mall.common.to.es.SkuEsModel;
import lombok.Data;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021-05-12
 * @Description: 封装返回到前端的搜索结果
 **/
@Data
public class SearchResultVo {

    /**
     * 查询到的保存在es中的商品信息
     */
    private List<SkuEsModel> products;

    /**
     * 分页信息
     */
    private Integer pageNum;

    private Long total;

    private Integer totalPages;

    /**
     * 可遍历的页码
     */
    private List<Integer> pageNavs;

    /**
     * 当前查询到的结果所有涉及到的品牌
     */
    private List<BrandVo> brands;

    /**
     * 所涉及到的分类id
     */
    private List<CatalogVo> catalogs;

    /**
     * 当前查询到的结果所涉及到的所有属性
     */
    private List<AttrVo> attrs;


    /**
     * 选中属性的面包屑
     */
    private List<BreadVo> breads;

    @Data
    public static class BreadVo{
        private String breadName;
        private String breadValue;
        private String link;
    }

    @Data
    public static class BrandVo{
        private Long brandId;
        private String brandName;
        private String brandImg;
    }

    @Data
    public static class CatalogVo{
        private Long catalogId;
        private String catalogName;
    }

    @Data
    public static class AttrVo{
        private Long attrId;
        private String attrName;
        private List<String> attrValues;  //一种属性可能对应多个属性值
    }

}
