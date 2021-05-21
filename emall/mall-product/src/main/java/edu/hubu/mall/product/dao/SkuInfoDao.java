package edu.hubu.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hubu.mall.product.entity.SkuInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description: 操作skuInfo数据
 **/
@Mapper
public interface SkuInfoDao extends BaseMapper<SkuInfoEntity> {
}
