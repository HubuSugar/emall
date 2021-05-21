package edu.hubu.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.product.entity.SkuSaleAttrValueEntity;
import edu.hubu.mall.product.vo.SkuItemSaleAttrVo;

import java.util.List;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-19
 **/
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    List<SkuItemSaleAttrVo> getSkuItemSaleAttrValuesBySpuId(Long spuId);
}
