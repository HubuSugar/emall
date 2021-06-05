package edu.hubu.mall.order.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import edu.hubu.mall.order.entity.OrderItemEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/5
 * @Description:
 **/
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {
}
