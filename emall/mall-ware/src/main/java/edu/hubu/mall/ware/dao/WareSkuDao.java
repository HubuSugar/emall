package edu.hubu.mall.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hubu.mall.ware.vo.WareSkuStockVo;
import edu.hubu.mall.ware.entity.WareSkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    List<WareSkuStockVo> queryStockCountBySkuIds(@Param("skuIds") List<Long> skuIds);
}
