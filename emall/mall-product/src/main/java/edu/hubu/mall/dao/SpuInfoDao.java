package edu.hubu.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hubu.mall.entity.SpuInfoEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/26
 * @Description: 操作spuInfo
 **/
@Mapper
public interface SpuInfoDao extends BaseMapper<SpuInfoEntity> {

    /**
     * 根据spuId，修改spuInfo的状态
     * * @param spuId
     * @param statusCode
     * @return
     */
    Integer updateSpuInfoStatusById(@Param("spuId") Long spuId, @Param("statusCode") int statusCode);
}
