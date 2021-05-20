package edu.hubu.mall.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hubu.mall.entity.SkuSaleAttrValueEntity;
import edu.hubu.mall.vo.AttrVo;
import edu.hubu.mall.vo.SkuItemSaleAttrVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: sku的销售属性操作接口
 * @Author: huxiaoge
 * @Date: 2021-05-19
 **/
@Mapper
public interface SkuSaleAttrValueDao extends BaseMapper<SkuSaleAttrValueEntity> {
    List<SkuItemSaleAttrVo> getSkuItemSaleAttrValuesBySpuId(@Param("spuId") Long spuId);
}
