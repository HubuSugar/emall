package edu.hubu.mall.ware.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hubu.mall.ware.vo.WareSkuStockVo;
import edu.hubu.mall.ware.entity.WareSkuEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
@Mapper
public interface WareSkuDao extends BaseMapper<WareSkuEntity> {

    List<WareSkuStockVo> queryStockCountBySkuIds(@Param("skuIds") List<Long> skuIds);

    /**
     * 根据skuIds查询当前这批订单项在哪些仓库有库存
     * @param skuIds
     * @return
     */
    List<WareSkuEntity> listWareSkuHashStock(@Param("skuIds") List<Long> skuIds);

    /**
     * 判断某件商品在某个仓库锁定几件是否成功
     * @param skuId 商品id
     * @param wareId 仓库id
     * @param num 锁定件数
     * @return
     */
    long lockSkuStock(@Param("skuId") Long skuId,@Param("wareId") Long wareId,@Param("num") Integer num);

    /**
     * 根据skuId、wareId退回锁定的库存
     * @param skuId
     * @param wareId
     * @param skuNum
     */
    void unlockSkuStock(@Param("skuId") Long skuId, @Param("wareId") Long wareId, @Param("skuNum") Integer skuNum);
}
