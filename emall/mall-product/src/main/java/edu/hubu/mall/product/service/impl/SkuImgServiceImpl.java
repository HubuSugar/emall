package edu.hubu.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.product.dao.SkuImgDao;
import edu.hubu.mall.product.entity.SkuImgEntity;
import edu.hubu.mall.product.service.SkuImgService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-19
 **/
@Service
public class SkuImgServiceImpl extends ServiceImpl<SkuImgDao,SkuImgEntity> implements SkuImgService {

    /**
     * 根据skuId查询sku图片集合
     * @param skuId
     * @return
     */
    @Override
    public List<SkuImgEntity> querySkuImgsById(Long skuId) {
        LambdaQueryWrapper<SkuImgEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuImgEntity::getSkuId,skuId);
        return this.list(queryWrapper);
    }
}
