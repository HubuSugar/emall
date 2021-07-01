package edu.hubu.mall.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hubu.mall.order.entity.OrderEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/5
 * @Description:
 **/
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
    void updateOrderStatus(@Param("orderSn") String orderSn, @Param("orderStatus") Integer orderStatus, @Param("payType") Integer payType);

}
