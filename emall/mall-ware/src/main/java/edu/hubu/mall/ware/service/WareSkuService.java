package edu.hubu.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.common.mq.StockLockTo;
import edu.hubu.mall.common.order.OrderVo;
import edu.hubu.mall.common.ware.WareLockResultVo;
import edu.hubu.mall.common.ware.WareSkuLockVo;
import edu.hubu.mall.ware.entity.WareSkuEntity;

import java.util.List;
import java.util.Map;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
public interface WareSkuService extends IService<WareSkuEntity> {
    /**
     * 根据skuIds查询每个skuId的库存数量
     * @param skuIds
     * @return
     */
    Map<Long, Boolean> getSkuHasStockBySkuIds(List<Long> skuIds);

    /**
     * 为每个订单锁定库存
     * @param skuLocks
     * @return
     */
    Boolean orderLock(WareSkuLockVo skuLocks);

    /**
     * 通过mq的消息解锁库存（库存锁定类型）
     */
    void unlockStock(StockLockTo stockLockTo);

    /**
     * 通过mq的消息解锁库存(订单类型)
     */
    void unlockStock(OrderVo stockLockTo);

}
