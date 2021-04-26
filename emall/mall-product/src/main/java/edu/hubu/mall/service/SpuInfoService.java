package edu.hubu.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.entity.SpuInfoEntity;

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
     * @param paramMap
     * @return
     */
    List<SpuInfoEntity> querySpuInfoPageByCondition(Map<String, Object> paramMap);
}
