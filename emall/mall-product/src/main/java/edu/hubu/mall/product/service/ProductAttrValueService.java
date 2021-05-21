package edu.hubu.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.product.entity.ProductAttrValueEntity;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description: spuId对应到的商品属性服务
 **/
public interface ProductAttrValueService extends IService<ProductAttrValueEntity> {

    /**
     * 根据spuId查询商品属性列表
     * @param spuId
     * @return
     */
    public List<ProductAttrValueEntity> queryProductAttrValuesBySpuId(Long spuId);
}
