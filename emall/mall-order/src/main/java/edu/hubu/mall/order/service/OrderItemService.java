package edu.hubu.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.order.entity.OrderItemEntity;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/5
 * @Description:
 **/
public interface OrderItemService extends IService<OrderItemEntity> {

    /**
     * 批量查询订单项数据
     */
    List<OrderItemEntity> queryOrderItemsBatch(List<Long> ids);

}
