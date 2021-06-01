package edu.hubu.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.common.product.SkuInfoVo;
import edu.hubu.mall.product.entity.SkuInfoEntity;
import edu.hubu.mall.product.vo.SkuItemVo;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
public interface SkuInfoService extends IService<SkuInfoEntity> {

    /**
     * 根据spuId查询skuInfo列表数据
     */
    List<SkuInfoEntity> querySkuInfoListBySpuId(Long spuId);

    /**
     * 根据skuId查询商品详情页需要返回的数据
     * @param skuId
     * @return
     */
    SkuItemVo querySkuDetailByIdAsync(Long skuId) throws ExecutionException, InterruptedException;

    /**
     * 根据skuId查询skuInfo
     * @param skuId
     * @return
     */
    SkuInfoVo querySkuInfoById(Long skuId);
}
