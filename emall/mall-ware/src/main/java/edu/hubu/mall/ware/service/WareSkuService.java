package edu.hubu.mall.ware.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.ware.WareSkuStockVo;
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
}
