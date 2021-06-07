package edu.hubu.mall.ware.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.common.order.OrderItemVo;
import edu.hubu.mall.common.ware.WareSkuLockVo;
import edu.hubu.mall.ware.exception.NoStockException;
import edu.hubu.mall.ware.vo.WareSkuStockVo;
import edu.hubu.mall.ware.dao.WareSkuDao;
import edu.hubu.mall.ware.entity.WareSkuEntity;
import edu.hubu.mall.ware.service.WareSkuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
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
    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public Boolean orderLock(WareSkuLockVo skuLocks) {
        //1、按照下单的收货地址，找到一个就近仓库，锁定库存
        //2、找到每个商品在哪个仓库都有库存
        List<OrderItemVo> locks = skuLocks.getLocks();
        List<Long> skuIds = locks.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
        /**
         * 每个商品对应有库存的仓库id集合
         */
        Map<Long, List<Long>> stockWareMap = listWareSkuHashStock(skuIds);
        if(stockWareMap == null){
            throw new NoStockException("部分商品库存不足");
        }
        //依次为每件商品锁定库存
        for (OrderItemVo lock : locks) {
            boolean lockStock = false;
            List<Long> wareIds = stockWareMap.get(lock.getSkuId());
            if(CollectionUtils.isEmpty(wareIds)){
                throw new NoStockException(lock.getSkuId());
            }
            for(Long wareId:wareIds){
               long count = this.baseMapper.lockSkuStock(lock.getSkuId(),wareId,lock.getCount());
               if(count == 1){
                    //锁成功
                   lockStock = true;
                   break;
               }
            }
            //对于当前这个skuId每个仓库都尝试锁库存没有成功，那么也没有锁定库存成功
            if(!lockStock){
                throw new NoStockException(lock.getSkuId());
            }
        }
        //所有商品都遍历完了
        return true;
    }

    /**
     * 查询每个订单项都在哪些仓库有库存
     * @param skuIds 商品ids
     * @return key skuId value--wareIds
     */
    private Map<Long,List<Long>> listWareSkuHashStock(List<Long> skuIds){
        List<WareSkuEntity> stockWares = this.baseMapper.listWareSkuHashStock(skuIds);
        if(CollectionUtils.isEmpty(stockWares)){
            return null;
        }
        Map<Long,List<Long>> stockMap = new HashMap<>();
        for (Long skuId : skuIds) {
            List<Long> wareIds = stockWares.stream().filter(item -> skuId.equals(item.getSkuId())).map(WareSkuEntity::getWareId)
                    .collect(Collectors.toList());
            stockMap.put(skuId,wareIds);
        }
        return stockMap;
    }

}
