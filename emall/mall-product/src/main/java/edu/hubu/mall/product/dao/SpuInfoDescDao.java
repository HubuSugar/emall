package edu.hubu.mall.product.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hubu.mall.product.entity.SpuInfoDescEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description: 商品详情页的spu介绍操作层
 * @Author: huxiaoge
 * @Date: 2021-05-19
 **/
@Mapper
public interface SpuInfoDescDao extends BaseMapper<SpuInfoDescEntity> {
}
