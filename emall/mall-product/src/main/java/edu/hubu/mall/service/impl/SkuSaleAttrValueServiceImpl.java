package edu.hubu.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.dao.SkuSaleAttrValueDao;
import edu.hubu.mall.entity.SkuSaleAttrValueEntity;
import edu.hubu.mall.service.SkuSaleAttrValueService;
import edu.hubu.mall.vo.AttrVo;
import edu.hubu.mall.vo.SkuItemSaleAttrVo;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
