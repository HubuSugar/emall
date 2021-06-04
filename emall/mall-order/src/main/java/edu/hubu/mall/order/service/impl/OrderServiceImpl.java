package edu.hubu.mall.order.service.impl;

import edu.hubu.mall.common.auth.HostHolder;
import edu.hubu.mall.common.member.MemberReceiveAddressVo;
import edu.hubu.mall.common.order.OrderItemVo;
import edu.hubu.mall.member.entity.MemberVo;
import edu.hubu.mall.order.Interceptor.LoginRequireInterceptor;
import edu.hubu.mall.order.feign.CartFeignService;
import edu.hubu.mall.order.feign.MemberFeignService;
import edu.hubu.mall.order.feign.WareFeignService;
import edu.hubu.mall.order.service.OrderService;
import edu.hubu.mall.order.vo.OrderConfirmVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-03
 **/
@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    /**
     * 查询订单确认页数据
     * //TODO feign在远程调用时会丢失请求头，feign 会创建一个新的请求头（请求头中没有cookie，导致调用购物车服务时没有登录）
     * //TODO 定义feign配置请求拦截器，每次发送远程请求前，加上请求头
     * //TODO feign远程调用在异步的情况下会丢失请求头
     * @return
     */
//    @Override
    public OrderConfirmVo confirmOrderSync() {

        OrderConfirmVo confirmVo = new OrderConfirmVo();
        //登录的用户信息
        MemberVo member = LoginRequireInterceptor.loginUser.get();

        //1.查询收货地址列表
        List<MemberReceiveAddressVo> address = memberFeignService.queryMemberReceiveAddressList(member.getId());
        confirmVo.setAddress(address);

        //2.查询用户的购物车选中的购物项
        List<OrderItemVo> orderItems = cartFeignService.queryMemberCartItems();
        confirmVo.setItems(orderItems);

        //3、积分信息
        confirmVo.setIntegration(member.getIntegration());

        //4、订单总额、优惠总额get方法自动计算
        return confirmVo;
    }

    /**
     * 使用异步编排方式查询订单确认数据
     * @return
     */
    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {

        OrderConfirmVo confirmVo = new OrderConfirmVo();
        //登录的用户信息
        MemberVo member = LoginRequireInterceptor.loginUser.get();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        //TODO 异步模式下feign会丢失上下文
        //1.查询收货地址列表
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            //重新开线程之后，同步主线程的请求头数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberReceiveAddressVo> address = memberFeignService.queryMemberReceiveAddressList(member.getId());
            confirmVo.setAddress(address);
        }, executor);

        //2.查询用户的购物车选中的购物项
        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            //重新开线程之后，同步主线程的请求头数据
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<OrderItemVo> orderItems = cartFeignService.queryMemberCartItems();
            confirmVo.setItems(orderItems);
        }, executor).thenRunAsync(() -> {
            List<OrderItemVo> items = confirmVo.getItems();
            if(CollectionUtils.isEmpty(items)){
                return;
            }
            List<Long> skuIds = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            Map<Long, Boolean> skuHasStock = wareFeignService.getSkuHasStock(skuIds);
            confirmVo.setStocks(skuHasStock);
        },executor);

        //3、积分信息
        confirmVo.setIntegration(member.getIntegration());

        //等两个异步任务执行完成
        CompletableFuture.allOf(addressFuture,cartFuture).get();

        //4、订单总额、优惠总额get方法自动计算

        return confirmVo;
    }



}
