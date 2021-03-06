package edu.hubu.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hubu.mall.product.entity.CategoryEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/21
 * @Description: 商品分类数据库操作
 **/
@Mapper
public interface CategoryDao extends BaseMapper<CategoryEntity> {
}
