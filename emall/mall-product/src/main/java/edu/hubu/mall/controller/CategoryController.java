package edu.hubu.mall.controller;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.entity.CategoryEntity;
import edu.hubu.mall.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/20
 * @Description: 商品分类服务
 **/
@RestController
@RequestMapping("/product/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list/tree")
    public Result<List<CategoryEntity>> list(){
        Result<List<CategoryEntity>> result = Result.ok();
        List<CategoryEntity> categoryEntityList = categoryService.listWithTree();
        result.setData(categoryEntityList);
        return result;
    }

}
