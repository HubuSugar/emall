package edu.hubu.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.common.constant.Constant;
import edu.hubu.mall.common.utils.PageUtil;
import edu.hubu.mall.common.utils.Query;
import edu.hubu.mall.common.utils.QueryParam;
import edu.hubu.mall.coupon.dao.SeckillSkuDao;
import edu.hubu.mall.coupon.entity.SeckillSessionEntity;
import edu.hubu.mall.coupon.entity.SeckillSkuEntity;
import edu.hubu.mall.coupon.service.SeckillSkuService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-07-09
 **/
@Service
public class SeckillSkuServiceImpl extends ServiceImpl<SeckillSkuDao, SeckillSkuEntity> implements SeckillSkuService {

    /**
     * 秒杀场次关联的商品
     * @param queryParam
     * @return
     */
    @Override
    public PageUtil<SeckillSkuEntity> querySeckillSkuList(QueryParam queryParam) {

        LambdaQueryWrapper<SeckillSkuEntity> queryWrapper = new LambdaQueryWrapper<>();
        if(queryParam.getKey() != null){
            queryWrapper.eq(SeckillSkuEntity::getId,queryParam.getKey());
        }
        if(!StringUtils.isEmpty(queryParam.getPromotionId())){
            queryWrapper.eq(SeckillSkuEntity::getPromotionId,queryParam.getPromotionId());
        }
        HashMap<String, Object> pageMap = new HashMap<>();
        pageMap.put(Constant.PAGE_NO,queryParam.getPageNo());
        pageMap.put(Constant.PAGE_SIZE,queryParam.getPageSize());

        IPage<SeckillSkuEntity> page = this.page(new Query<SeckillSkuEntity>().getPage(pageMap),queryWrapper);
        return new PageUtil<>(page);
    }

    /**
     * 场次与商品的新增或者修改
     * @param skuEntity
     * @return
     */
    @Override
    public boolean saveOrUpdateSeckillSession(SeckillSkuEntity skuEntity) {
        //新增操作
        if(skuEntity.getId() == null){
            this.save(skuEntity);
        }else{
            SeckillSkuEntity old = this.getById(skuEntity.getId());
            if(old == null){
                return false;
            }
            skuEntity.setId(old.getId());
            this.updateById(skuEntity);
        }
        return true;
    }
}
