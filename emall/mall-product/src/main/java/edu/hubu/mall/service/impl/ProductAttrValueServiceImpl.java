package edu.hubu.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.dao.ProductAttrValueDao;
import edu.hubu.mall.entity.ProductAttrValueEntity;
import edu.hubu.mall.service.ProductAttrValueService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
@Service
public class ProductAttrValueServiceImpl extends ServiceImpl<ProductAttrValueDao, ProductAttrValueEntity> implements ProductAttrValueService {

    /**
     * 根据spuId查询商品属性
     * @param spuId
     * @return
     */
    @Override
    public List<ProductAttrValueEntity> queryProductAttrValuesBySpuId(Long spuId) {
        LambdaQueryWrapper<ProductAttrValueEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ProductAttrValueEntity::getSpuId,spuId);
        return this.list(queryWrapper);
    }
}
