package edu.hubu.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.product.entity.CategoryEntity;
import edu.hubu.mall.product.vo.Catalog2Vo;

import java.util.List;
import java.util.Map;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/21
 * @Description:
 **/
public interface CategoryService extends IService<CategoryEntity> {

    /**
     * 查询商品分类数据，按树形结构展示
     * @return
     */
    List<CategoryEntity> listWithTree();

    /**
     * 根据id批量查询分类数据
     * @param cataLogIds
     * @return
     */
    List<CategoryEntity> queryCategoriesByIds(List<Long> cataLogIds);

    /**
     * 查询所有的顶级分类
     */
    List<CategoryEntity> queryTopCategories();

    /**
     * 查询前端分类数据
     */
    Map<String,List<Catalog2Vo>> queryCatalogJson();
}
