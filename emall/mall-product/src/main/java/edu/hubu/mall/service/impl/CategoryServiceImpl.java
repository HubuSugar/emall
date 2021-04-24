package edu.hubu.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.common.Result;
import edu.hubu.mall.dao.CategoryDao;
import edu.hubu.mall.entity.CategoryEntity;
import edu.hubu.mall.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;
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
