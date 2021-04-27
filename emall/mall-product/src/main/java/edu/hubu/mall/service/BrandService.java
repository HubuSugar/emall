package edu.hubu.mall.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.entity.BrandEntity;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
public interface BrandService extends IService<BrandEntity> {

    /**
     * 批量查询品牌数据
     * @param BrandIds
     * @return
     */
    List<BrandEntity> queryBrandsByIds(List<Long> BrandIds);
}
