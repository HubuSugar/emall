package edu.hubu.mall.product.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021-04-29
 * @Description:
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Catalog2Vo {

    /**
     * 顶级分类的id
     */
    private String catalog1Id;

    /**
     * 包含的三级分类列表
     */
    private List<CatalogLeaf> catalog3List;

    /**
     * 当前分类id
     */
    private String id;

    /**
     * 当前分类的名称
     */
    private String name;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CatalogLeaf{
        /**
         * 二级分类的id
         */
        private String catalog2Id;
        /**
         * 三级分类的id
         */
        private String id;
        /**
         * 三级分类的名称
         */
        private String name;
    }
}
