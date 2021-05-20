package edu.hubu.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.entity.SkuSaleAttrValueEntity;
import edu.hubu.mall.vo.AttrVo;
import edu.hubu.mall.vo.SkuItemSaleAttrVo;

import java.util.List;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-19
 **/
public interface SkuSaleAttrValueService extends IService<SkuSaleAttrValueEntity> {

    List<SkuItemSaleAttrVo> getSkuItemSaleAttrValuesBySpuId(Long spuId);
}
