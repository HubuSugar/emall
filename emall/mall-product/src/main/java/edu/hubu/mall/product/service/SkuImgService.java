package edu.hubu.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.product.entity.SkuImgEntity;

import java.util.List;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-19
 **/
public interface SkuImgService extends IService<SkuImgEntity> {
    /**
     * 根据skuId查询sku图片集合
     * @param skuId
     * @return
     */
    List<SkuImgEntity> querySkuImgsById(Long skuId);
}
