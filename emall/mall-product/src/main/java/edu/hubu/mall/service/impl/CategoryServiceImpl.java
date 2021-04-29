package edu.hubu.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.common.constant.Constant;
import edu.hubu.mall.common.constant.ProductConstant;
import edu.hubu.mall.dao.CategoryDao;
import edu.hubu.mall.entity.CategoryEntity;
import edu.hubu.mall.service.CategoryService;
import edu.hubu.mall.vo.Catalog2Vo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/21
 * @Description: 商品分类服务
 **/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Override
    public List<CategoryEntity> listWithTree() {
        //查出所有的分类数据
        List<CategoryEntity> categories = baseMapper.selectList(null);

        //将查询出的菜单数据包装成树形结构
        List<CategoryEntity> categoryList = categories.stream().filter(category -> category.getParentCid() == 0).peek(menu -> {
            menu.setChildren(getChildrens(menu, categories));
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return categoryList;
    }

    /**
     * 根据分类Id批量查询分类数据
     * @param cataLogIds
     * @return
     */
    @Override
    public List<CategoryEntity> queryCategoriesByIds(List<Long> cataLogIds) {
        LambdaQueryWrapper<CategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CategoryEntity::getCatId,cataLogIds);
        return this.list(queryWrapper);
    }


    /**
     * 查询所有的顶级分类
     * @return
     */
    @Override
    public List<CategoryEntity> queryTopCategories() {
        LambdaQueryWrapper<CategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CategoryEntity::getParentCid, ProductConstant.PRODUCT_TOP_CATALOGID);
        return list(queryWrapper);
    }

    /**
     * 查询前端需要的分类json数据
     * @return
     */
    @Override
    public Map<String, List<Catalog2Vo>> queryCatalogJson() {
        //1.只查询一次数据库，一次将所有分类数据查询过来
        List<CategoryEntity> categories = baseMapper.selectList(null);

        //2.1 筛选出所有的一级分类节点
        List<CategoryEntity> topCategories = getChildrens(categories, ProductConstant.PRODUCT_TOP_CATALOGID);

        //2.2 封装数据
        return topCategories.stream().collect(Collectors.toMap(k -> String.valueOf(k.getCatId()), v -> {
            //2.3 查询二级菜单数据
            return getChildrens(categories, v.getCatId()).stream().map(l2 -> {
                //2.4 查询三级菜单列表
                List<Catalog2Vo.CatalogLeaf> catalogLeafList = getChildrens(categories, l2.getCatId()).stream().map(l3 -> {
                    return  new Catalog2Vo.CatalogLeaf(String.valueOf(l2.getCatId()),String.valueOf(l3.getCatId()),l3.getName());
                }).collect(Collectors.toList());

                return new Catalog2Vo(String.valueOf(v.getCatId()),catalogLeafList,String.valueOf(l2.getCatId()),l2.getName());
            }).collect(Collectors.toList());
        }));
    }


    private List<CategoryEntity> getChildrens(List<CategoryEntity> allCategories,Long pid){
        return  allCategories.stream().filter(item -> item.getParentCid().equals(pid)).collect(Collectors.toList());
    }

    /**
     * 根据一个分类查找子分类
     * @param root 当前菜单节点
     * @param categories 所有分类数据
     * @return 子节点结果集
     */
    private List<CategoryEntity> getChildrens(CategoryEntity root,List<CategoryEntity> categories){
        List<CategoryEntity> childrens = categories.stream()
                .filter(category -> category.getParentCid().equals(root.getCatId()))
                .peek(menu -> menu.setChildren(getChildrens(menu, categories)))
                .sorted((menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                }).collect(Collectors.toList());
        return childrens;
    }
}
