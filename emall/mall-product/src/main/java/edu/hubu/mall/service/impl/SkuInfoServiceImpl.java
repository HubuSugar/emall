package edu.hubu.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.common.to.es.SkuEsModel;
import edu.hubu.mall.dao.SkuInfoDao;
import edu.hubu.mall.entity.SkuInfoEntity;
import edu.hubu.mall.service.SkuInfoService;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    /**
     * 根据spuId查询要上架的skuInfoList数据
     * @param spuId spuId
     * @return
     */
    @Override
    public List<SkuInfoEntity> querySkuInfoListBySpuId(Long spuId) {
        LambdaQueryWrapper<SkuInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuInfoEntity::getSpuId,spuId);
        return list(queryWrapper);
    }
}
