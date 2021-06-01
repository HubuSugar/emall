package edu.hubu.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.product.dao.SkuSaleAttrValueDao;
import edu.hubu.mall.product.entity.SkuSaleAttrValueEntity;
import edu.hubu.mall.product.service.SkuSaleAttrValueService;
import edu.hubu.mall.product.vo.SkuItemSaleAttrVo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-19
 **/
@Service
public class SkuSaleAttrValueServiceImpl extends ServiceImpl<SkuSaleAttrValueDao, SkuSaleAttrValueEntity>  implements SkuSaleAttrValueService {
    /**
     * 根据spuId查询当前spuId下的所有sku的销售的值的聚合结果
     * @param spuId
     * @return
     */
    @Override
    public List<SkuItemSaleAttrVo> getSkuItemSaleAttrValuesBySpuId(Long spuId) {
        return this.baseMapper.getSkuItemSaleAttrValuesBySpuId(spuId);
    }

    /**
     * 加入购物车时远程查询商品的销售属性
     * @param skuId
     * @return
     */
    @Override
    public List<String> querySkuSaleAttrValuesAsString(Long skuId) {
        LambdaQueryWrapper<SkuSaleAttrValueEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuSaleAttrValueEntity::getSkuId,skuId);
        List<SkuSaleAttrValueEntity> saleAttrs = this.baseMapper.selectList(queryWrapper);
        return saleAttrs.stream().map(item -> {
            return item.getAttrName() + ":" + item.getAttrValue();
        }).collect(Collectors.toList());
    }
}
