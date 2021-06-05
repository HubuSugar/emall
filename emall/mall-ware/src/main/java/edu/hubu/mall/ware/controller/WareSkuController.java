package edu.hubu.mall.ware.controller;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.ware.WareLockResultVo;
import edu.hubu.mall.common.ware.WareSkuLockVo;
import edu.hubu.mall.ware.service.WareSkuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description: sku库存
 **/
@RestController
@RequestMapping("/ware/wareSku")
public class WareSkuController {

    @Autowired
    private WareSkuService wareSkuService;

    /**
     * 根据skuIds列表查询是否有库存
     * @param skuIds skuId列表
     * @return skuId - 是否有库存
     */
    @PostMapping(value="/hasStock")
    public Map<Long,Boolean> getSkuHasStock(@RequestBody List<Long> skuIds){
        return wareSkuService.getSkuHasStockBySkuIds(skuIds);
    }

    /**
     * 为某一个订单锁定库存
     * @reutrn 每一个商品的库存锁定结果
     */
    @PostMapping("/order/lockWare")
    public WareLockResultVo orderLock(@RequestBody WareSkuLockVo skuLocks){
        return  wareSkuService.orderLock(skuLocks);
    }

}
