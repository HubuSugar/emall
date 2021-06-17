package edu.hubu.mall.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.common.order.OrderVo;
import edu.hubu.mall.order.entity.OrderEntity;
import edu.hubu.mall.order.to.OrderSubmitTo;
import edu.hubu.mall.order.vo.OrderConfirmVo;
import edu.hubu.mall.order.vo.OrderSubmitResultVo;

import java.util.concurrent.ExecutionException;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-03
 **/
public interface OrderService extends IService<OrderEntity> {

    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

    OrderSubmitResultVo submitOrder(OrderSubmitTo to);

    OrderVo queryInfoByOrderSn(String orderSn);
}
