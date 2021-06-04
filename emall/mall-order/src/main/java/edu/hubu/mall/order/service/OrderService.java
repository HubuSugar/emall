package edu.hubu.mall.order.service;

import edu.hubu.mall.order.vo.OrderConfirmVo;

import java.util.concurrent.ExecutionException;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-03
 **/
public interface OrderService {

    OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException;

}
