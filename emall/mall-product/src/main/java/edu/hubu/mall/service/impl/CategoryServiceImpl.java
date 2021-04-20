package edu.hubu.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.dao.CategoryDao;
import edu.hubu.mall.entity.Category;
import edu.hubu.mall.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/21
 * @Description: 商品分类服务
 **/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, Category> implements CategoryService {

    @Override
    public List<Category> listTree() {
        return this.baseMapper.selectList(null);
    }
}
