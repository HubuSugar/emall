package edu.hubu.mall.order.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.common.auth.MemberVo;
import edu.hubu.mall.common.constant.OrderConstant;
import edu.hubu.mall.common.enums.OrderStatus;
import edu.hubu.mall.common.member.MemberReceiveAddressVo;
import edu.hubu.mall.common.order.OrderItemVo;
import edu.hubu.mall.common.product.SpuInfoVo;
import edu.hubu.mall.common.ware.FareVo;
import edu.hubu.mall.common.ware.WareLockResultVo;
import edu.hubu.mall.common.ware.WareSkuLockVo;
import edu.hubu.mall.order.Interceptor.LoginRequireInterceptor;
import edu.hubu.mall.order.dao.OrderDao;
import edu.hubu.mall.order.entity.OrderEntity;
import edu.hubu.mall.order.entity.OrderItemEntity;
import edu.hubu.mall.order.feign.CartFeignService;
import edu.hubu.mall.order.feign.MemberFeignService;
import edu.hubu.mall.order.feign.ProductFeignService;
import edu.hubu.mall.order.feign.WareFeignService;
import edu.hubu.mall.order.service.OrderItemService;
import edu.hubu.mall.order.service.OrderService;
import edu.hubu.mall.order.to.OrderCreatedTo;
import edu.hubu.mall.order.to.OrderSubmitTo;
import edu.hubu.mall.order.vo.OrderConfirmVo;
import edu.hubu.mall.order.vo.OrderSubmitResultVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-03
 **/
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderDao,OrderEntity> implements OrderService {

    /**
     * 在同一个线程共享用户提交的订单数据
     */
    private ThreadLocal<OrderSubmitTo> confirmOrderThreadLocal = new ThreadLocal<>();

    @Autowired
    MemberFeignService memberFeignService;

    @Autowired
    CartFeignService cartFeignService;

    @Autowired
    WareFeignService wareFeignService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    OrderItemService orderItemService;

    /**
     * 查询订单确认页数据
     * //TODO feign在远程调用时会丢失请求头，feign 会创建一个新的请求头（请求头中没有cookie，导致调用购物车服务时没有登录）
     * //TODO 定义feign配置请求拦截器，每次发送远程请求前，加上请求头
     * //TODO feign远程调用在异步的情况下会丢失请求头
     *
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
     * 使用异步编排方式查询订单确认数据,生成防重复令牌，存redis，发送到页面
     *
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
            if (CollectionUtils.isEmpty(items)) {
                return;
            }
            List<Long> skuIds = items.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
            Map<Long, Boolean> skuHasStock = wareFeignService.getSkuHasStock(skuIds);
            confirmVo.setStocks(skuHasStock);
        }, executor);

        //3、积分信息
        confirmVo.setIntegration(member.getIntegration());

        //等两个异步任务执行完成
        CompletableFuture.allOf(addressFuture, cartFuture).get();

        //4、订单总额、优惠总额get方法自动计算

        //TODO 生成防重令牌,使用随机码作为防重复提交令牌
        String uuid = IdUtil.simpleUUID();
        //将令牌返回给页面，同时将令牌存入redis
        String tokenKey = OrderConstant.ORDER_TOKEN_PREFIX + member.getId();
        redisTemplate.opsForValue().set(tokenKey, uuid, OrderConstant.ORDER_TOKEN_TIMEOUT, TimeUnit.MINUTES);
        confirmVo.setOrderToken(uuid);

        return confirmVo;
    }

    /**
     * 处理订单提交逻辑
     *
     * @param to 订单提交数据
     * @return 提交成功之后返回数据
     */
    @Transactional
    @Override
    public OrderSubmitResultVo submitOrder(OrderSubmitTo to) {

        /**
         * 在当前线程保存提交的订单数据
         */
        confirmOrderThreadLocal.set(to);

        OrderSubmitResultVo submitResultVo = new OrderSubmitResultVo();
        //用户信息
        MemberVo memberVo = LoginRequireInterceptor.loginUser.get();

        //首先验证令牌
        String orderTokenKey = OrderConstant.ORDER_TOKEN_PREFIX + memberVo.getId();
        //用户提交的token和redis查到的token
        String orderToken = to.getOrderToken();
        String redisToken = redisTemplate.opsForValue().get(orderTokenKey);

        //原子验证令牌并删除的lua脚本,返回0 - 1，0 表示失败；1 -- 表示失败
        String redisScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long res = redisTemplate.execute(new DefaultRedisScript<>(redisScript, Long.class), Arrays.asList(orderToken, redisToken));
        // TODO 未保证原子性
        // if(orderToken != null && orderToken.equals(redisToken)){
        //     //验证令牌通过
        //     redisTemplate.delete(orderTokenKey)
        // }else{
        //     //不通过
        // }
        if (res != 0) {
            //1、创建订单
            OrderCreatedTo order = creareOrder();
            //2、开始验价
            BigDecimal userTotalPrice = to.getPayPrice();
            BigDecimal totalAmount = order.getOrder().getTotalAmount();
            if(Math.abs(totalAmount.subtract(userTotalPrice).doubleValue()) < OrderConstant.ORDER_PRICE_ALLOW_GAP){
                //3、价格误差允许，保存订单数据
               boolean result =  saveOrder(order);
               //4、保存成功就开始锁定库存，只要有异常就回滚订单数据
               //订单号，订单项，哪一个商品，需要锁住多少件库存
                WareSkuLockVo lockVo = new WareSkuLockVo();
                lockVo.setOrderSn(order.getOrder().getOrderSn());

                List<OrderItemEntity> orderItems = order.getOrderItems();
                List<OrderItemVo> collect = orderItems.stream().map(item -> {
                    OrderItemVo itemVo = new OrderItemVo();
                    itemVo.setCount(item.getSkuQuantity());
                    itemVo.setSkuId(item.getSkuId());
                    itemVo.setTitle(item.getSkuName());
                    return itemVo;
                }).collect(Collectors.toList());
                lockVo.setLocks(collect);

                WareLockResultVo lockResult = wareFeignService.orderLock(lockVo);
                if(lockResult.getCode() == 0){
                    //锁定成功
                }else{
                    //锁定失败

                }


            }else{
                 submitResultVo.setCode(2);
            }

        } else {
            //验证失败,令牌校验不通过
            submitResultVo.setCode(1);
        }

        return submitResultVo;
    }

    /**
     * 保存订单和订单项数据
     * @param creareOrder
     * @return
     */
    private boolean saveOrder(OrderCreatedTo creareOrder) {
        OrderEntity order = creareOrder.getOrder();
        order.setModifyTime(new Date());
        order.setCreateTime(new Date());
        this.save(order);
        List<OrderItemEntity> orderItems = creareOrder.getOrderItems();
        orderItemService.saveBatch(orderItems);
        return false;
    }

    //创建订单的方法
    private OrderCreatedTo creareOrder() {

        OrderCreatedTo createdTo = new OrderCreatedTo();

        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        log.info("orderSn:{}", snowflake.nextId());
        String orderSn = String.valueOf(snowflake.nextId());
        //1、构建订单信息
        OrderEntity orderEntity = buildOrder(orderSn);

        //2、构建所有的订单项信息
        List<OrderItemEntity> orderItems = buildOrderItems(orderSn);

        //3、计算优惠、积分信息
        computePrice(orderEntity,orderItems);
        createdTo.setOrder(orderEntity);
        createdTo.setOrderItems(orderItems);

        return createdTo;
    }

    /**
     * 计算价格
     * @param orderEntity
     * @param orderItems
     */
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItems) {
        BigDecimal total = new BigDecimal("0");
        BigDecimal coupon = new BigDecimal("0");
        BigDecimal promotion = new BigDecimal("0");
        BigDecimal integration = new BigDecimal("0");
        Integer growth = 0;  //成长值
        Integer integra = 0;  //积分

        if(!CollectionUtils.isEmpty(orderItems)){
            for (OrderItemEntity item:orderItems) {
                total = total.add(item.getRealAmount());
                coupon = coupon.add(item.getCouponAmount());
                promotion = promotion.add(item.getPromotionAmount());
                integration = integration.add(item.getIntegrationAmount());
                growth += item.getGiftGrowth();
                integra += item.getGiftIntegration();
            }
        }
        orderEntity.setTotalAmount(total);
        orderEntity.setPromotionAmount(promotion);
        orderEntity.setCouponAmount(coupon);
        orderEntity.setIntegrationAmount(integration);
        orderEntity.setGrowth(growth);
        orderEntity.setIntegration(integra);

        //应付总额，实际总额 + 运费
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));

    }


    /**
     * 构建所有的订单项信息
     */
    private List<OrderItemEntity> buildOrderItems(String orderSn) {

        List<OrderItemVo> orderItems = cartFeignService.queryMemberCartItems();
        if (!CollectionUtils.isEmpty(orderItems)) {
           return orderItems.stream().map(item -> {
               OrderItemEntity itemEntity = buildOrderItem(item);
               itemEntity.setOrderSn(orderSn);

               return  itemEntity;
            }).collect(Collectors.toList());
        }

        return null;
    }

    /**
     * 构建每一个订单项
     * @param item
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo item) {
        OrderItemEntity itemEntity = new OrderItemEntity();
        //1、远程查询spuInfo
        SpuInfoVo spuInfo = productFeignService.querySpuInfoBySkuId(item.getSkuId());
        itemEntity.setSpuId(spuInfo.getId());
        itemEntity.setSpuName(spuInfo.getSpuName());
        itemEntity.setSpuBrand(spuInfo.getBrandId().toString());
        itemEntity.setCategoryId(spuInfo.getCatalogId());

        //2、sku信息
        itemEntity.setSkuId(item.getSkuId());
        itemEntity.setSkuName(item.getTitle());
        itemEntity.setSkuPrice(item.getPrice());
        itemEntity.setSkuAttrsVals(String.join(";",item.getSkuAttr()));
        itemEntity.setSkuQuantity(item.getCount());
        itemEntity.setSkuPic(item.getImage());

        //3、优惠信息
        itemEntity.setPromotionAmount(new BigDecimal("0"));
        itemEntity.setCouponAmount(new BigDecimal("0"));
        itemEntity.setIntegrationAmount(new BigDecimal("0"));

        //4、积分信息,以商品的价钱作为商品的积分
        BigDecimal origin = itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
        itemEntity.setGiftGrowth(origin.intValue());
        itemEntity.setGiftIntegration(origin.intValue());

        //5、订单项的实际价格信息
        BigDecimal realAmount = origin.subtract(itemEntity.getPromotionAmount()).subtract(itemEntity.getCouponAmount()).subtract(itemEntity.getIntegrationAmount());
        itemEntity.setRealAmount(realAmount);

        return itemEntity;
    }

    /**
     * 创建订单信息
     *
     * @param orderSn
     * @return
     */
    private OrderEntity buildOrder(String orderSn) {
        //创建订单号
        OrderEntity entity = new OrderEntity();

        //设置用户的id
        entity.setMemberId(LoginRequireInterceptor.loginUser.get().getId());
        //设置订单号
        entity.setOrderSn(orderSn);

        OrderSubmitTo orderSubmitTo = confirmOrderThreadLocal.get();
        FareVo fare = wareFeignService.getFare(orderSubmitTo.getAddrId());
        //设置费用
        entity.setFreightAmount(fare.getFare());
        //设置收货人信息
        MemberReceiveAddressVo addressVo = fare.getMemberReceiveAddressVo();
        entity.setReceiverName(addressVo.getName());
        entity.setReceiverPhone(addressVo.getPhone());
        entity.setReceiverProvince(addressVo.getProvince());
        entity.setReceiverCity(addressVo.getCity());
        entity.setReceiverRegion(addressVo.getRegion());
        entity.setReceiverDetailAddress(addressVo.getDetailAddress());
        entity.setReceiverPostCode(addressVo.getPostCode());

        //设置订单的状态
        entity.setStatus(OrderStatus.CREATE_NEW.getCode());
        entity.setAutoConfirmDay(OrderConstant.ORDER_AUTO_CONFIRM_DAYS);
        entity.setConfirmStatus(0);

        //设置删除状态(0-未删除，1-已删除)
        entity.setDeleteStatus(0);

        return entity;
    }


}
