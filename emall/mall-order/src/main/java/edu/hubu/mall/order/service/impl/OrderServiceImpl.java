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
     * ???????????????????????????????????????????????????
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
     * ???????????????????????????
     * //TODO feign???????????????????????????????????????feign ???????????????????????????????????????????????????cookie????????????????????????????????????????????????
     * //TODO ??????feign?????????????????????????????????????????????????????????????????????
     * //TODO feign???????????????????????????????????????????????????
     *
     * @return
     */
//    @Override
    public OrderConfirmVo confirmOrderSync() {

        OrderConfirmVo confirmVo = new OrderConfirmVo();
        //?????????????????????
        MemberVo member = LoginRequireInterceptor.loginUser.get();

        //1.????????????????????????
        List<MemberReceiveAddressVo> address = memberFeignService.queryMemberReceiveAddressList(member.getId());
        confirmVo.setAddress(address);

        //2.??????????????????????????????????????????
        List<OrderItemVo> orderItems = cartFeignService.queryMemberCartItems();
        confirmVo.setItems(orderItems);

        //3???????????????
        confirmVo.setIntegration(member.getIntegration());

        //4??????????????????????????????get??????????????????
        return confirmVo;
    }

    /**
     * ????????????????????????????????????????????????,???????????????????????????redis??????????????????
     *
     * @return
     */
    @Override
    public OrderConfirmVo confirmOrder() throws ExecutionException, InterruptedException {

        OrderConfirmVo confirmVo = new OrderConfirmVo();
        //?????????????????????
        MemberVo member = LoginRequireInterceptor.loginUser.get();

        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

        //TODO ???????????????feign??????????????????
        //1.????????????????????????
        CompletableFuture<Void> addressFuture = CompletableFuture.runAsync(() -> {
            //?????????????????????????????????????????????????????????
            RequestContextHolder.setRequestAttributes(requestAttributes);
            List<MemberReceiveAddressVo> address = memberFeignService.queryMemberReceiveAddressList(member.getId());
            confirmVo.setAddress(address);
        }, executor);

        //2.??????????????????????????????????????????
        CompletableFuture<Void> cartFuture = CompletableFuture.runAsync(() -> {
            //?????????????????????????????????????????????????????????
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

        //3???????????????
        confirmVo.setIntegration(member.getIntegration());

        //?????????????????????????????????
        CompletableFuture.allOf(addressFuture, cartFuture).get();

        //4??????????????????????????????get??????????????????

        //TODO ??????????????????,??????????????????????????????????????????
        String uuid = IdUtil.simpleUUID();
        //????????????????????????????????????????????????redis
        String tokenKey = OrderConstant.ORDER_TOKEN_PREFIX + member.getId();
        redisTemplate.opsForValue().set(tokenKey, uuid, OrderConstant.ORDER_TOKEN_TIMEOUT, TimeUnit.MINUTES);
        confirmVo.setOrderToken(uuid);

        return confirmVo;
    }

    /**
     * ????????????????????????
     * TODO ??????????????????????????????????????????
     * @param to ??????????????????
     * @return ??????????????????????????????
     */
//    @GlobalTransactional
//    @Transactional
//    @Override
    public OrderSubmitResultVo submitOrderBySeata(OrderSubmitTo to) {
        /**
         * ??????????????????????????????????????????
         */
        confirmOrderThreadLocal.set(to);

        OrderSubmitResultVo submitResultVo = new OrderSubmitResultVo();
        //????????????
        MemberVo memberVo = LoginRequireInterceptor.loginUser.get();

        //??????????????????
        String orderTokenKey = OrderConstant.ORDER_TOKEN_PREFIX + memberVo.getId();
        //???????????????token???redis?????????token
        String orderToken = to.getOrderToken();
//        String redisToken = redisTemplate.opsForValue().get(orderTokenKey);

        //??????????????????????????????lua??????,??????0 - 1???0 ???????????????1 -- ????????????
        String redisScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long res = redisTemplate.execute(new DefaultRedisScript<>(redisScript, Long.class), Arrays.asList(orderTokenKey), orderToken);
        // TODO ??????????????????
        // if(orderToken != null && orderToken.equals(redisToken)){
        //     //??????????????????
        //     redisTemplate.delete(orderTokenKey)
        // }else{
        //     //?????????
        // }
        if (res != 0) {
            //1???????????????
            OrderCreatedTo order = creareOrder();
            //2???????????????
            BigDecimal userTotalPrice = to.getPayPrice();
            BigDecimal paymentAmount = order.getOrder().getPayAmount();
            if(Math.abs(paymentAmount.subtract(userTotalPrice).doubleValue()) < OrderConstant.ORDER_PRICE_ALLOW_GAP){
                //3??????????????????????????????????????????
               boolean result =  saveOrder(order);
               //4???????????????????????????????????????????????????????????????????????????
               //?????????????????????????????????????????????????????????????????????
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

                //TODO 1?????????????????????,??????????????????????????????????????????????????????
                WareLockResultVo lockResult = wareFeignService.orderLock(lockVo);
                if(lockResult.getCode() == 0){
                    //????????????
                    submitResultVo.setCode(0);
                    submitResultVo.setOrder(order.getOrder());

                    //TODO 2??????????????????????????????????????????????????????????????????????????????????????????????????????TODO1,???????????????????????????????????????
                    //TODO ???????????????(1) ??????seata????????????????????????????????????????????????????????????????????????????????????????????? ???2????????????????????????????????????????????????????????????
                    //?????????????????????????????????????????? (????????????????????????????????????????????????????????????????????????)
                     //int i = 10 / 0;

                    return submitResultVo;
                }else{
                    //??????????????????
//                    submitResultVo.setCode(3);
                    //TODO ????????????????????????????????????????????????????????????????????????????????????????????????TODO1???????????????
                    throw new NoStockException("??????????????????");
                }
            }else{
                 submitResultVo.setCode(2);
            }
        } else {
            //????????????,?????????????????????
            submitResultVo.setCode(1);
        }
        return submitResultVo;
    }

    /**
     * ??????????????????????????????????????????????????????????????????????????????????????????????????????
     * @param to,
     * @return
     */
    @Transactional
    @Override
    public OrderSubmitResultVo submitOrder(OrderSubmitTo to) {
        /**
         * ??????????????????????????????????????????
         */
        confirmOrderThreadLocal.set(to);
        OrderSubmitResultVo submitResultVo = new OrderSubmitResultVo();
        //????????????,?????????????????????????????????????????????????????????
        MemberVo memberVo = LoginRequireInterceptor.loginUser.get();

        //??????????????????
        String orderTokenKey = OrderConstant.ORDER_TOKEN_PREFIX + memberVo.getId();
        //???????????????token???redis?????????token
        String orderToken = to.getOrderToken();

        //??????????????????????????????lua??????,??????0 - 1???0 ???????????????1 -- ????????????
        String redisScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
        Long res = redisTemplate.execute(new DefaultRedisScript<>(redisScript, Long.class), Arrays.asList(orderTokenKey), orderToken);
        if (res != 0) {
            //1????????????????????????????????????????????????
            OrderCreatedTo order = creareOrder();
            //2???????????????
            BigDecimal userTotalPrice = to.getPayPrice();
            BigDecimal paymentAmount = order.getOrder().getPayAmount();
            if(Math.abs(paymentAmount.subtract(userTotalPrice).doubleValue()) < OrderConstant.ORDER_PRICE_ALLOW_GAP){
                //3??????????????????????????????????????????
                boolean result =  saveOrder(order);
                //4???????????????????????????????????????????????????????????????????????????
                //?????????????????????????????????????????????????????????????????????
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

                //????????????????????????????????????
                //TODO 1?????????????????????,??????????????????????????????????????????????????????
                WareLockResultVo lockResult = wareFeignService.orderLock(lockVo);
                if(lockResult.getCode() == 0){
                    //????????????
                    submitResultVo.setCode(0);
                    submitResultVo.setOrder(order.getOrder());

                    //TODO 2??????????????????????????????????????????????????????????????????????????????????????????????????????TODO1,???????????????????????????????????????
                    //TODO ???????????????(1) ??????seata????????????????????????????????????????????????????????????????????????????????????????????? ???2????????????????????????????????????????????????????????????
                    //?????????????????????????????????????????? (????????????????????????????????????????????????????????????????????????)
                    //?????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
                    // int i = 10 / 0;

                    //TODO ????????????????????????mq????????????
                    rabbitTemplate.convertAndSend(OrderConstant.ORDER_EVENT_EXCHANGE,OrderConstant.ORDER_CREATE_ROUTE,order.getOrder());

                    return submitResultVo;
                }else{
                    //??????????????????
//                    submitResultVo.setCode(3);
                    //TODO ????????????????????????????????????????????????????????????????????????????????????????????????TODO1???????????????
                    throw new NoStockException("??????????????????");
                }
            }else{
                submitResultVo.setCode(2);
            }
        } else {
            //????????????,?????????????????????
            submitResultVo.setCode(1);
        }
        return submitResultVo;
    }

    /**
     * ?????????????????????????????????
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
     * ????????????????????????????????????
     * ????????????????????????????????????????????????????????????????????????
     * ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
     * @param order
     */
    @Override
    public void closeOrder(OrderEntity order) {

        OrderEntity entity = getById(order.getId());
        if(OrderStatus.CREATE_NEW.getCode().equals(entity.getStatus())){
            OrderEntity update = new OrderEntity();
            update.setId(order.getId());
            //????????????????????????4???????????????????????????????????????????????????????????????
            update.setStatus(OrderStatus.CANCLED.getCode());
            updateById(update);

            //??????????????????????????????????????????????????????????????????????????????
            //TODO ?????????????????????????????????????????????????????????????????????????????????
            OrderVo vo = new OrderVo();
            BeanUtils.copyProperties(order,vo);
            rabbitTemplate.convertAndSend(WareConstant.WARE_EVENT_EXCHANGE,OrderConstant.ORDER_RELEASE_OTHER_ROUTE,vo);
        }

    }

    /**
     * ???????????????????????????????????????
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
        //???????????????????????????
        BigDecimal payAmount = vo.getPayAmount().setScale(2, RoundingMode.HALF_UP);
        payVo.setTotal_amount(String.valueOf(payAmount));

        LambdaQueryWrapper<OrderItemEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(OrderItemEntity::getOrderSn,orderSn);
        List<OrderItemEntity> orderItemEntities = orderItemService.getBaseMapper().selectList(queryWrapper);
        if(CollectionUtils.isEmpty(orderItemEntities)){
            payVo.setSubject("?????????");
            return payVo;
        }
        OrderItemEntity orderItemEntity = orderItemEntities.get(0);
        payVo.setSubject(orderItemEntity.getSkuName());
        payVo.setBody(orderItemEntity.getSkuAttrsVals());
        return payVo;
    }

    /**
     * ?????????????????????????????????
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

        //??????????????????????????????????????????
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
     * ????????????????????????
     * @param payAsynVo
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public String handleOrderPayed(PayAsynVo payAsynVo) {
        //??????????????????
        PaymentInfoEntity paymentInfo = new PaymentInfoEntity();
        paymentInfo.setOrderSn(payAsynVo.getOut_trade_no());
        paymentInfo.setAlipayTradeNo(payAsynVo.getTrade_no());
        paymentInfo.setTotalAmount(new BigDecimal(payAsynVo.getBuyer_pay_amount()));
        paymentInfo.setSubject(payAsynVo.getBody());
        paymentInfo.setPaymentStatus(payAsynVo.getTrade_status());
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setCallbackTime(payAsynVo.getNotify_time());

        paymentInfoService.save(paymentInfo);
        //???????????????????????????????????????
        if("TRADE_SUCCESS".equals(payAsynVo.getTrade_status()) || "TRADE_FINISHED".equals(payAsynVo.getTrade_status())){
            String orderSn = payAsynVo.getOut_trade_no();
            this.updateOrderStatus(orderSn,OrderStatus.PAYED.getCode(), PayConstant.ALIPAY);
        }

        return "success";
    }

    /**
     * ?????????????????????????????????
     */
    @Override
    public void updateOrderStatus(String orderSn,Integer orderStatus,Integer payType){
        this.baseMapper.updateOrderStatus(orderSn,orderStatus,payType);
    }

    /**
     * ??????????????????
     * @param orderTo
     */
    @Override
    public void createSeckillOrder(SeckillOrderTo orderTo) {
        //TODO ??????????????????
        OrderEntity orderEntity = new OrderEntity();
        orderEntity.setOrderSn(orderTo.getOrderSn());
        orderEntity.setMemberId(orderTo.getMemberId());
        orderEntity.setCreateTime(new Date());
        BigDecimal totalPrice = orderTo.getSeckillPrice().multiply(BigDecimal.valueOf(orderTo.getNum()));
        orderEntity.setPayAmount(totalPrice);
        orderEntity.setStatus(OrderStatus.CREATE_NEW.getCode());

        //????????????
        this.save(orderEntity);

        //?????????????????????
        OrderItemEntity orderItem = new OrderItemEntity();
        orderItem.setOrderSn(orderTo.getOrderSn());
        orderItem.setRealAmount(totalPrice);

        orderItem.setSkuQuantity(orderTo.getNum());

        //???????????????spu??????
        SpuInfoVo spuInfoVo = productFeignService.querySpuInfoBySkuId(orderTo.getSkuId());

        orderItem.setSpuId(spuInfoVo.getId());
        orderItem.setSpuName(spuInfoVo.getSpuName());
        orderItem.setSpuBrand(spuInfoVo.getBrandName());
        orderItem.setCategoryId(spuInfoVo.getCatalogId());

        //?????????????????????
        orderItemService.save(orderItem);
    }

    /**
     * ??????????????????????????????
     * @param creareOrder
     * @return
     */
    private boolean saveOrder(OrderCreatedTo creareOrder) {
        OrderEntity order = creareOrder.getOrder();
        order.setModifyTime(new Date());
        order.setCreateTime(new Date());
        this.save(order);
        List<OrderItemEntity> orderItems = creareOrder.getOrderItems();
        //????????????id
        orderItems.forEach(item -> {
            item.setOrderId(order.getId());
        });
        orderItemService.saveBatch(orderItems);
        return true;
    }

    //?????????????????????
    private OrderCreatedTo creareOrder() {

        OrderCreatedTo createdTo = new OrderCreatedTo();

        Snowflake snowflake = IdUtil.createSnowflake(1, 1);
        log.info("orderSn:{}", snowflake.nextId());
        String orderSn = String.valueOf(snowflake.nextId());
        //1?????????????????????
        OrderEntity orderEntity = buildOrder(orderSn);

        //2?????????????????????????????????
        List<OrderItemEntity> orderItems = buildOrderItems(orderSn);

        //3??????????????????????????????
        computePrice(orderEntity,orderItems);
        createdTo.setOrder(orderEntity);
        createdTo.setOrderItems(orderItems);

        return createdTo;
    }

    /**
     * ????????????
     * @param orderEntity
     * @param orderItems
     */
    private void computePrice(OrderEntity orderEntity, List<OrderItemEntity> orderItems) {
        BigDecimal total = new BigDecimal("0");
        BigDecimal coupon = new BigDecimal("0");
        BigDecimal promotion = new BigDecimal("0");
        BigDecimal integration = new BigDecimal("0");
        Integer growth = 0;  //?????????
        Integer integra = 0;  //??????

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

        //??????????????????????????? + ??????
        orderEntity.setPayAmount(total.add(orderEntity.getFreightAmount()));
    }


    /**
     * ??????????????????????????????
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
     * ????????????????????????
     * @param item
     * @return
     */
    private OrderItemEntity buildOrderItem(OrderItemVo item) {
        OrderItemEntity itemEntity = new OrderItemEntity();
        //1???????????????spuInfo
        SpuInfoVo spuInfo = productFeignService.querySpuInfoBySkuId(item.getSkuId());
        itemEntity.setSpuId(spuInfo.getId());
        itemEntity.setSpuName(spuInfo.getSpuName());
        itemEntity.setSpuBrand(spuInfo.getBrandId().toString());
        itemEntity.setCategoryId(spuInfo.getCatalogId());

        //2???sku??????
        itemEntity.setSkuId(item.getSkuId());
        itemEntity.setSkuName(item.getTitle());
        itemEntity.setSkuPrice(item.getPrice());
        itemEntity.setSkuAttrsVals(String.join(";",item.getSkuAttr()));
        itemEntity.setSkuQuantity(item.getCount());
        itemEntity.setSkuPic(item.getImage());

        //3???????????????
        itemEntity.setPromotionAmount(new BigDecimal("0"));
        itemEntity.setCouponAmount(new BigDecimal("0"));
        itemEntity.setIntegrationAmount(new BigDecimal("0"));

        //4???????????????,???????????????????????????????????????
        BigDecimal origin = itemEntity.getSkuPrice().multiply(new BigDecimal(itemEntity.getSkuQuantity().toString()));
        itemEntity.setGiftGrowth(origin.intValue());
        itemEntity.setGiftIntegration(origin.intValue());

        //5?????????????????????????????????
        BigDecimal realAmount = origin.subtract(itemEntity.getPromotionAmount()).subtract(itemEntity.getCouponAmount()).subtract(itemEntity.getIntegrationAmount());
        itemEntity.setRealAmount(realAmount);

        return itemEntity;
    }

    /**
     * ??????????????????
     *
     * @param orderSn
     * @return
     */
    private OrderEntity buildOrder(String orderSn) {
        //???????????????
        OrderEntity entity = new OrderEntity();

        //???????????????id
        entity.setMemberId(LoginRequireInterceptor.loginUser.get().getId());
        entity.setMemberUsername(LoginRequireInterceptor.loginUser.get().getUsername());
        //???????????????
        entity.setOrderSn(orderSn);

        OrderSubmitTo orderSubmitTo = confirmOrderThreadLocal.get();
        FareVo fare = wareFeignService.getFare(orderSubmitTo.getAddrId());
        //????????????
        entity.setFreightAmount(fare.getFare());
        //?????????????????????
        MemberReceiveAddressVo addressVo = fare.getMemberReceiveAddressVo();
        entity.setReceiverName(addressVo.getName());
        entity.setReceiverPhone(addressVo.getPhone());
        entity.setReceiverProvince(addressVo.getProvince());
        entity.setReceiverCity(addressVo.getCity());
        entity.setReceiverRegion(addressVo.getRegion());
        entity.setReceiverDetailAddress(addressVo.getDetailAddress());
        entity.setReceiverPostCode(addressVo.getPostCode());

        //?????????????????????
        entity.setStatus(OrderStatus.CREATE_NEW.getCode());
        entity.setAutoConfirmDay(OrderConstant.ORDER_AUTO_CONFIRM_DAYS);
        entity.setConfirmStatus(0);

        //??????????????????(0-????????????1-?????????)
        entity.setDeleteStatus(0);

        return entity;
    }


}
