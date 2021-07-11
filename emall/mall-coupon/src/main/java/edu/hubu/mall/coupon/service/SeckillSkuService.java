package edu.hubu.mall.coupon.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.common.utils.PageUtil;
import edu.hubu.mall.common.utils.QueryParam;
import edu.hubu.mall.coupon.entity.SeckillSkuEntity;

import java.util.List;
import java.util.Set;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-07-09
 **/
public interface SeckillSkuService extends IService<SeckillSkuEntity> {
    PageUtil<SeckillSkuEntity> querySeckillSkuList(QueryParam queryParam);

    boolean saveOrUpdateSeckillSession(SeckillSkuEntity skuEntity);

    List<SeckillSkuEntity> querySeckillSkuByIds(Set<Long> sessionIds);
}
