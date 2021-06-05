package edu.hubu.mall.order.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.order.dao.OrderItemDao;
import edu.hubu.mall.order.entity.OrderItemEntity;
import edu.hubu.mall.order.service.OrderItemService;
import org.springframework.stereotype.Service;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/5
 * @Description:
 **/
@Service
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {
}
