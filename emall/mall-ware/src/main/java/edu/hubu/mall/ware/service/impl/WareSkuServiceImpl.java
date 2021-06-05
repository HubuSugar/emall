package edu.hubu.mall.ware.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.common.ware.WareLockResultVo;
import edu.hubu.mall.common.ware.WareSkuLockVo;
import edu.hubu.mall.ware.vo.WareSkuStockVo;
import edu.hubu.mall.ware.dao.WareSkuDao;
import edu.hubu.mall.ware.entity.WareSkuEntity;
import edu.hubu.mall.ware.service.WareSkuService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description: sku库存服务
 **/
@Service
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    /**
     * 根据skuIds查询库存数量
     * @param skuIds
     * @return
     */
    @Override
    public Map<Long, Boolean> getSkuHasStockBySkuIds(List<Long> skuIds) {
        List<WareSkuStockVo> wareSkuStockVos = baseMapper.queryStockCountBySkuIds(skuIds);
        return wareSkuStockVos.stream().collect(Collectors.toMap(WareSkuStockVo::getSkuId,
                item -> {
                    return item.getSkuStockCount() != null && item.getSkuStockCount() > 0;
        }));
    }

    /**
     * 为某个订单锁定库存
     * @param skuLocks 需要锁库存的订单项
     * @return
     */
    @Override
    public List<WareLockResultVo> orderLock(WareSkuLockVo skuLocks) {
        return null;
    }
}
