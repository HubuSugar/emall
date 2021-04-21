package edu.hubu.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.entity.Category;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/21
 * @Description:
 **/
public interface CategoryService extends IService<Category> {

    /**
     * 查询商品分类数据，按树形结构展示
     * @return
     */
    List<Category> listWithTree();
}
