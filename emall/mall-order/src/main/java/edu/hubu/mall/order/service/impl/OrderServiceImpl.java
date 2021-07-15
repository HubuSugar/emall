package edu.hubu.mall.order.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.common.auth.MemberVo;
import edu.hubu.mall.common.constant.OrderConstant;
import edu.hubu.mall.common.constant.PayConstant;
import edu.hubu.mall.common.constant.WareConstant;
import edu.hubu.mall.common.enums.OrderStatus;
import edu.hubu.mall.common.exception.NoStockException;
import edu.hubu.mall.common.member.MemberReceiveAddressVo;
import edu.hubu.mall.common.order.OrderItemVo;
import edu.hubu.mall.common.order.OrderVo;
import edu.hubu.mall.common.product.SpuInfoVo;
import edu.hubu.mall.common.seckill.SeckillOrderTo;
import edu.hubu.mall.common.utils.PageUtil;
import edu.hubu.mall.common.utils.Query;
import edu.hubu.mall.common.ware.FareVo;
import edu.hubu.mall.common.ware.WareLockResultVo;
import edu.hubu.mall.common.ware.WareSkuLockVo;
import edu.hubu.mall.order.Interceptor.LoginRequireInterceptor;
import edu.hubu.mall.order.dao.OrderDao;
import edu.hubu.mall.order.entity.OrderEntity;
import edu.hubu.mall.order.entity.OrderItemEntity;
import edu.hubu.mall.order.entity.PaymentInfoEntity;
import edu.hubu.mall.order.feign.CartFeignService;
import edu.hubu.mall.order.feign.MemberFeignService;
import edu.hubu.mall.order.feign.ProductFeignService;
import edu.hubu.mall.order.feign.WareFeignService;
import edu.hubu.mall.order.service.OrderItemService;
import edu.hubu.mall.order.service.OrderService;
import edu.hubu.mall.order.service.PaymentInfoService;
import edu.hubu.mall.order.to.OrderCreatedTo;
import edu.hubu.mall.order.to.OrderSubmitTo;
import edu.hubu.mall.order.vo.OrderConfirmVo;
import edu.hubu.mall.order.vo.OrderSubmitResultVo;
import edu.hubu.mall.order.vo.PayAsynVo;
import edu.hubu.mall.order.vo.PayVo;
import io.seata.spring.annotation.GlobalTransactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
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
    private final ThreadLocal<OrderSubmitTo> confirmOrderThreadLocal = new ThreadLocal<>();

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

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    PaymentInfoService paymentInfoService;

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
     * TODO 通过分布式事务保证数据一致性
     * @param to 订单提交数据
     * @return 提交成功之后返回数据
     */
//    @GlobalTransactional
//    @Transactional
//    @Override
    public OrderSubmitResultVo submitOrderBySeata(OrderSubmitTo to) {
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
//        String redisToken = redisTemplate.opsForValue().get(orderTokenKey);

        //原子验证令牌并删除的lua脚本,返回0 - 1，0 表示失败；1 -- 表示失败
        String redisScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long res = redisTemplate.execute(new DefaultRedisScript<>(redisScript, Long.class), Arrays.asList(orderTokenKey), orderToken);
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
            BigDecimal paymentAmount = order.getOrder().getPayAmount();
            if(Math.abs(paymentAmount.subtract(userTotalPrice).doubleValue()) < OrderConstant.ORDER_PRICE_ALLOW_GAP){
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

                //TODO 1、如果此处异常,会导致创建的订单和订单项数据无法回滚
                WareLockResultVo lockResult = wareFeignService.orderLock(lockVo);
                if(lockResult.getCode() == 0){
                    //锁定成功
                    submitResultVo.setCode(0);
                    submitResultVo.setOrder(order.getOrder());

                    //TODO 2、如果此处出现异常（如扣减积分异常，网络异常），那么抛出异常只能解决TODO1,无法回滚订单锁定的库存数据
                    //TODO 解决方案：(1) 通过seata分布式事务；（加锁机制：对于电商系统这种高并发的项目并不合适） （2）通过柔性事务，消息队列保证最终的一致性
                    //下面异常用来模拟扣减积分异常 (此时订单和订单项数据没有创建，但是库存依然锁定了)
                     //int i = 10 / 0;

                    return submitResultVo;
                }else{
                    //库存锁定失败
//                    submitResultVo.setCode(3);
                    //TODO 通过抛出异常，让整个事务回滚（包括创建的订单和订单项数据）（解决TODO1处的问题）
                    throw new NoStockException("锁定库存异常");
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
     * 使用消息队列实现数据最终一致性，保证本地事务，出现异常时订单创建回滚
     * @param to,
     * @return
     */
    @Transactional
    @Override
    public OrderSubmitResultVo submitOrder(OrderSubmitTo to) {
        /**
         * 在当前线程保存提交的订单数据
         */
        confirmOrderThreadLocal.set(to);
        OrderSubmitResultVo submitResultVo = new OrderSubmitResultVo();
        //用户信息,如果能提交订单信息，说明是存在登录信息
        MemberVo memberVo = LoginRequireInterceptor.loginUser.get();

        //首先验证令牌
        String orderTokenKey = OrderConstant.ORDER_TOKEN_PREFIX + memberVo.getId();
        //用户提交的token和redis查到的token
        String orderToken = to.getOrderToken();

        //原子验证令牌并删除的lua脚本,返回0 - 1，0 表示失败；1 -- 表示失败
        String redisScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long res = redisTemplate.execute(new DefaultRedisScript<>(redisScript, Long.class), Arrays.asList(orderTokenKey), orderToken);
        if (res != 0) {
            //1、创建订单，需要调用购物车微服务
            OrderCreatedTo order = creareOrder();
            //2、开始验价
            BigDecimal userTotalPrice = to.getPayPrice();
            BigDecimal paymentAmount = order.getOrder().getPayAmount();
            if(Math.abs(paymentAmount.subtract(userTotalPrice).doubleValue()) < OrderConstant.ORDER_PRICE_ALLOW_GAP){
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

                //调用库存微服务，锁定库存
                //TODO 1、如果此处异常,会导致创建的订单和订单项数据无法回滚
                WareLockResultVo lockResult = wareFeignService.orderLock(lockVo);
                if(lockResult.getCode() == 0){
                    //锁定成功
                    submitResultVo.setCode(0);
                    submitResultVo.setOrder(order.getOrder());

                    //TODO 2、如果此处出现异常（如扣减积分异常，网络异常），那么抛出异常只能解决TODO1,无法回滚订单锁定的库存数据
                    //TODO 解决方案：(1) 通过seata分布式事务；（加锁机制：对于电商系统这种高并发的项目并不合适） （2）通过柔性事务，消息队列保证最终的一致性
                    //下面异常用来模拟扣减积分异常 (此时订单和订单项数据没有创建，但是库存依然锁定了)
                    //锁定库存也成功了，锁定库存的消息已经发送到了消息队列，但是执行后面的业务逻辑出现了异常，模拟使用消息，释放库存
                    // int i = 10 / 0;

                    //TODO 订单创建成功，给mq发送消息
                    rabbitTemplate.convertAndSend(OrderConstant.ORDER_EVENT_EXCHANGE,OrderConstant.ORDER_CREATE_ROUTE,order.getOrder());

                    return submitResultVo;
                }else{
                    //库存锁定失败
//                    submitResultVo.setCode(3);
                    //TODO 通过抛出异常，让整个事务回滚（包括创建的订单和订单项数据）（解决TODO1处的问题）
                    throw new NoStockException("锁定库存异常");
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
     * 根据订单号查询订单信息
     * @param orderSn
     * @return
     */
    @Override
    public OrderVo queryInfoByOrderSn(String orderSn) {
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_sn",orderSn);
        OrderEntity orderEntity = baseMapper.selectOne(queryWrapper);
        if(orderEntity == null){
            return null;
        }
        OrderVo vo = new OrderVo();
        BeanUtils.copyProperties(orderEntity,vo);
        return vo;
    }

    /**
     * 订单超过三十分钟自动关闭
     * 关单之前查询订单状态，只有是新创建的订单才能关闭
     * 因为此处的每一个订单消息都是从消息队列过来，有可能该订单已经被支付过了。。。
     * @param order
     */
    @Override
    public void closeOrder(OrderEntity order) {

        OrderEntity entity = getById(order.getId());
        if(OrderStatus.CREATE_NEW.getCode().equals(entity.getStatus())){
            OrderEntity update = new OrderEntity();
            update.setId(order.getId());
            //将订单状态设置为4，库存服务收到库存消息时会触发库存解锁操作
            update.setStatus(OrderStatus.CANCLED.getCode());
            updateById(update);

            //订单关单之后主动给库存释放队列发送一个解锁库存的消息
            //TODO 防止订单消息卡顿，导致库存解锁消息比订单关单消息先消费
            OrderVo vo = new OrderVo();
            BeanUtils.copyProperties(order,vo);
            rabbitTemplate.convertAndSend(WareConstant.WARE_EVENT_EXCHANGE,OrderConstant.ORDER_RELEASE_OTHER_ROUTE,vo);
        }

    }

    /**
     * 根据订单号查询订单支付信息
     * @param orderSn
     * @return
     */
    @Override
    public PayVo getPayOrder(String orderSn) {

        PayVo payVo = new PayVo();
        OrderVo vo = queryInfoByOrderSn(orderSn);
        if(vo == null){
            return payVo;
        }

        payVo.setOut_trade_no(orderSn);
        //支付宝保留两位小数
        BigDecimal payAmount = vo.getPayAmount().setScale(2, RoundingMode.HALF_UP);
        payVo.setTotal_amount(String.valueOf(payAmount));

        LambdaQueryWrapper<OrderItemEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderItemEntity::getOrderSn,orderSn);
        List<OrderItemEntity> orderItemEntities = orderItemService.getBaseMapper().selectList(queryWrapper);
        if(CollectionUtils.isEmpty(orderItemEntities)){
            payVo.setSubject("知托付");
            return payVo;
        }
        OrderItemEntity orderItemEntity = orderItemEntities.get(0);
        payVo.setSubject(orderItemEntity.getSkuName());
        payVo.setBody(orderItemEntity.getSkuAttrsVals());
        return payVo;
    }

    /**
     * 分页查询用户的订单信息
     * @param args
     * @return
     */
    @Override
    public PageUtil<OrderVo> queryMemberOrderList(Map<String, Object> args) {

        MemberVo memberVo = LoginRequireInterceptor.loginUser.get();
        if(memberVo == null){
            return null;
        }
        QueryWrapper<OrderEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("member_id",memberVo.getId());
        IPage<OrderEntity> page = this.page(new Query<OrderEntity>().getPage(args), queryWrapper);

        //查询订单列表的所有订单项信息
        List<Long> orderIds = page.getRecords().stream().map(OrderEntity::getId).collect(Collectors.toList());
        List<OrderItemEntity> orderItemEntities = orderItemService.queryOrderItemsBatch(orderIds);

        List<OrderVo> orders = page.getRecords().stream().map(item -> {
            List<OrderItemEntity> items = orderItemEntities.stream().filter(orderItem -> item.getId().equals(orderItem.getOrderId())).collect(Collectors.toList());
            List<OrderItemVo> itemVos = items.stream().map(itemVo -> {
                OrderItemVo orderItemVo = new OrderItemVo();
                orderItemVo.setSkuId(itemVo.getSkuId());
                orderItemVo.setTitle(itemVo.getSkuName());
                orderItemVo.setPrice(itemVo.getSkuPrice());
                orderItemVo.setImage(itemVo.getSkuPic());
                orderItemVo.setCount(itemVo.getSkuQuantity());
                return orderItemVo;
            }).collect(Collectors.toList());

            OrderVo orderVo = new OrderVo();
            BeanUtils.copyProperties(item, orderVo, "orderItems");
            orderVo.setOrderItems(itemVos);

            return orderVo;
        }).collect(Collectors.toList());
        return new PageUtil<OrderVo>(orders,page.getTotal(),page.getSize(),page.getCurrent());
    }

    /**
     * 支付宝付款成功后
     * @param payAsynVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String handleOrderPayed(PayAsynVo payAsynVo) {
        //保存交易流水
        PaymentInfoEntity paymentInfo = new PaymentInfoEntity();
        paymentInfo.setOrderSn(payAsynVo.getOut_trade_no());
        paymentInfo.setAlipayTradeNo(payAsynVo.getTrade_no());
        paymentInfo.setTotalAmount(new BigDecimal(payAsynVo.getBuyer_pay_amount()));
        paymentInfo.setSubject(payAsynVo.getBody());
        paymentInfo.setPaymentStatus(payAsynVo.getTrade_status());
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setCallbackTime(payAsynVo.getNotify_time());

        paymentInfoService.save(paymentInfo);
        //如果支付成功，更新订单状态
        if("TRADE_SUCCESS".equals(payAsynVo.getTrade_status()) || "TRADE_FINISHED".equals(payAsynVo.getTrade_status())){
            String orderSn = payAsynVo.getOut_trade_no();
            this.updateOrderStatus(orderSn,OrderStatus.PAYED.getCode(), PayConstant.ALIPAY);
        }

        return "success";
    }

    /**
     * 根据订单号更新订单状态
     */
    @Override
    public void updateOrderStatus(String orderSn,Integer orderStatus,Integer payType){
        this.baseMapper.updateOrderStatus(orderSn,orderStatus,payType);
    }

    /**
     * 创建秒杀订单
     * @param orderTo
     */
    @Override
    public void createSeckillOrder(SeckillOrderTo orderTo) {
        //TODO 保存订单信息
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderTo.getOrderSn());
        orderEntity.setMemberId(orderTo.getMemberId());
        orderEntity.setCreateTime(new Date());
        BigDecimal totalPrice = orderTo.getSeckillPrice().multiply(BigDecimal.valueOf(orderTo.getNum()));
        orderEntity.setPayAmount(totalPrice);
        orderEntity.setStatus(OrderStatus.CREATE_NEW.getCode());

        //保存订单
        this.save(orderEntity);

        //保存订单项信息
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrderSn(orderTo.getOrderSn());
        orderItem.setRealAmount(totalPrice);

        orderItem.setSkuQuantity(orderTo.getNum());

        //保存商品的spu信息
        SpuInfoVo spuInfoVo = productFeignService.querySpuInfoBySkuId(orderTo.getSkuId());

        orderItem.setSpuId(spuInfoVo.getId());
        orderItem.setSpuName(spuInfoVo.getSpuName());
        orderItem.setSpuBrand(spuInfoVo.getBrandName());
        orderItem.setCategoryId(spuInfoVo.getCatalogId());

        //保存订单项数据
        orderItemService.save(orderItem);
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
        //设置订单id
        orderItems.forEach(item -> {
            item.setOrderId(order.getId());
        });
        orderItemService.saveBatch(orderItems);
        return true;
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
        entity.setMemberUsername(LoginRequireInterceptor.loginUser.get().getUsername());
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
