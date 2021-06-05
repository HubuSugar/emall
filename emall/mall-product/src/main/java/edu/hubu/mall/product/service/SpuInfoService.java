package edu.hubu.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.common.product.SpuInfoVo;
import edu.hubu.mall.product.entity.SpuInfoEntity;
import java.util.List;
import java.util.Map;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/26
 * @Description:
 **/
public interface SpuInfoService extends IService<SpuInfoEntity> {
    /**
     * 根据页面条件分页查询spuInfo数据
     * @param paramMap 查询条件参数
     * @return
     */
    List<SpuInfoEntity> querySpuInfoPageByCondition(Map<String, Object> paramMap);

    Boolean spuUp(Long spuId);

    /**
     * 根据skuId查询spuInfo
     * @param skuId
     * @return
     */
    SpuInfoVo querySpuInfoBySkuId(Long skuId);
}
