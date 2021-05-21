package edu.hubu.mall.product.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.product.dao.BrandDao;
import edu.hubu.mall.product.entity.BrandEntity;
import edu.hubu.mall.product.service.BrandService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
@Service
public class BrandServiceImpl extends ServiceImpl<BrandDao,BrandEntity> implements BrandService {

    /**
     * 根据品牌id批量查询品牌实体
     * @param brandIds
     * @return
     */
    @Override
    public List<BrandEntity> queryBrandsByIds(List<Long> brandIds) {
        LambdaQueryWrapper<BrandEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(BrandEntity::getBrandId,brandIds);
        return this.list(queryWrapper);
    }
}
