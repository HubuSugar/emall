package edu.hubu.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.entity.SkuInfoEntity;

import java.util.List;

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

}
