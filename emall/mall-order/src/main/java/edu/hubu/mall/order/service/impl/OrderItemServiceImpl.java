package edu.hubu.mall.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.order.dao.OrderItemDao;
import edu.hubu.mall.order.entity.OrderItemEntity;
import edu.hubu.mall.order.service.OrderItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/5
 * @Description:
 **/
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {
    /**
     * 批量查询订单项数据
     * @param ids
     * @return
     */
    @Override
    public List<OrderItemEntity> queryOrderItemsBatch(List<Long> ids) {
        LambdaQueryWrapper<OrderItemEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(OrderItemEntity::getOrderId,ids);
        return this.baseMapper.selectList(queryWrapper);
    }
}
