package edu.hubu.mall.seckill.service.impl;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.auth.MemberVo;
import edu.hubu.mall.common.constant.OrderConstant;
import edu.hubu.mall.common.constant.SeckillConstant;
import edu.hubu.mall.common.product.SkuInfoVo;
import edu.hubu.mall.common.seckill.SeckillOrderTo;
import edu.hubu.mall.common.seckill.SeckillSessionVo;
import edu.hubu.mall.common.seckill.SeckillSkuVo;
import edu.hubu.mall.seckill.feign.CouponFeignService;
import edu.hubu.mall.seckill.feign.ProductFeignService;
import edu.hubu.mall.seckill.interceptor.LoginRequireInterceptor;
import edu.hubu.mall.seckill.service.SeckillService;
import edu.hubu.mall.seckill.to.SeckillSkuTo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-07-11
 **/
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    CouponFeignService couponFeignService;

    @Autowired
    ProductFeignService productFeignService;

    @Autowired
    StringRedisTemplate redisTemplate;

    @Autowired
    RedissonClient redissonClient;

    @Autowired
    RabbitTemplate rabbitTemplate;



    /**
     * 上架近三天的商品
     * 主要是将秒杀的商品保存到缓存中
     * 1、扫描哪些活动参与秒杀
     */
    @Override
    public void uploadSeckillSkuLatest3Days() {
        //1、查到最近3天的秒杀活动
        Result<List<SeckillSessionVo>> sessionResult = couponFeignService.getLatest3DaysSessions();
        if(sessionResult.getCode() != 0){
            log.info("查询秒杀场次失败");
            return;
        }
        List<SeckillSessionVo> records = sessionResult.getData();
        if(CollectionUtils.isEmpty(records)){
            log.info("未查询到参与秒杀的活动场次");
            return;
        }
        //2、缓存活动的场次信息
        saveSessions(records);

        //3、缓存活动关联的商品信息
        saveRelationSkus(records);
        log.info("秒杀商品上架完成！！！");
    }

    /**
     * 根据当前时间查询秒杀的商品列表
     * @return
     */
    @Override
    public List<SeckillSkuTo> getCurrentSeckillSkus() {
        //获取当前时间
        long time = new Date().getTime();
        //获取所有秒杀场次的键
        Set<String> keys = redisTemplate.keys(SeckillConstant.SESSIONS_CACHE_PREFIX + "*");
        if(CollectionUtils.isEmpty(keys)){
            return null;
        }
        for (String key : keys) {
            String replace = key.replace(SeckillConstant.SESSIONS_CACHE_PREFIX, "");
            String[] s = replace.split("_");
            long startTime = Long.parseLong(s[0]);
            long endTime = Long.parseLong(s[1]);
            if(time >= startTime && time <= endTime){
                //从当前时间对应到的场次中找到对应的秒杀场次
                List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(SeckillConstant.SECKILL_SKU_CACHE_PREFIX);
                if(CollectionUtils.isEmpty(range)){
                    return null;
                }
                List<String> skus = ops.multiGet(range);
                if(!CollectionUtils.isEmpty(skus)){
                    return skus.stream().map(item -> {
                        return JSON.parseObject(item.toString(), SeckillSkuTo.class);
                    }).collect(Collectors.toList());
                }
                break;
            }
        }
        return null;
    }

    /**
     * 查询当前商品的秒杀信息
     * @param skuId
     * @return
     */
    @Override
    public SeckillSkuVo querySeckillSkuInfo(Long skuId) {
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(SeckillConstant.SECKILL_SKU_CACHE_PREFIX);
        Set<String> keys = ops.keys();
        if(CollectionUtils.isEmpty(keys)){
            return null;
        }
        String regx = "\\d_" + skuId;
        SeckillSkuVo skuVo = new SeckillSkuVo();
        for (String key : keys) {
            if(key.matches(regx)){
                //这里取出来的是，秒杀上架时的SeckillSkuTo对象
                String sku = ops.get(key);
                SeckillSkuTo seckillSkuTo = JSONObject.parseObject(sku, SeckillSkuTo.class);
                if(seckillSkuTo != null)
                BeanUtils.copyProperties(seckillSkuTo,skuVo);
                long now = new Date().getTime();
                long startTime =  seckillSkuTo.getStartTime();
                long endTime = seckillSkuTo.getEndTime();
                if(now < startTime || now > endTime){
                    //说明当前商品没在秒杀中,那么将该商品的随机码置空，在秒杀时间段在开放
                    skuVo.setRandomCode(null);
                }
                return  skuVo;

            }

        }
        return null;
    }

    /**
     * 执行秒杀逻辑
     * 请求的合法性校验：
     * 1、登录校验（拦截器实现）
     * 2、随机码是否正确
     * 3、幂等性校验（一个用户只能秒杀一次）
     * 4、秒杀数量是否超过限制
     * 5、是否在秒杀时间范围内
     * @param killId
     * @param key
     * @param num
     * @return
     */
    @Override
    public String kill(String killId, String key, Integer num) {
        long s1 = System.currentTimeMillis();
        MemberVo memberVo = LoginRequireInterceptor.loginUser.get();
        BoundHashOperations<String, String, String> ops = redisTemplate.boundHashOps(SeckillConstant.SECKILL_SKU_CACHE_PREFIX);
        String json = ops.get(killId);
        SeckillSkuTo skuTo = JSONObject.parseObject(json, SeckillSkuTo.class);
        if(skuTo == null){
            log.info("未查询到秒杀商品");
            return null;
        }
        //校验时间的合法性：当前时间在秒杀时间范围内
        long time = new Date().getTime();
        long startTime = skuTo.getStartTime();
        long endTime = skuTo.getEndTime();
        if(time < startTime || time > endTime){
            log.info("不在秒杀时间范围内");
            return null;
        }
        //场次信息合法性校验
        String kill = skuTo.getPromotionSessionId() + "_" + skuTo.getSkuId();
        if(!killId.equals(kill)){
            log.info("场次信息不符合");
            return null;
        }
        //校验随机码
        String randomCode = skuTo.getRandomCode();
        if(!key.equals(randomCode)){
            log.info("随机码不正确");
            return null;
        }
        String stock = redisTemplate.opsForValue().get(SeckillConstant.SECKILL_STOCK_PREFIX + randomCode);
        if(StringUtils.isEmpty(stock)){
            log.info("库存异常");
            return null;
        }
        int stockCount = Integer.parseInt(stock);
        //数量校验
        Integer limit = skuTo.getSeckillLimit();
        //1、库存量大于0 2、秒杀数量小于限制的数量  3、秒杀的数量小于库存的数量
        if(stockCount <= 0 || num > limit || num > stockCount){
            log.info("数量不合法");
            return null;
        }
        //TODO 开始进行秒杀，尝试获取信号量
        String seckillUserKey = SeckillConstant.SECKILL_USER_PREFIX + String.format("%s_%s",memberVo.getId(),kill) ;
        long ttl = endTime - System.currentTimeMillis();
        Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(seckillUserKey, String.valueOf(num), ttl, TimeUnit.MILLISECONDS);
        if(aBoolean != null && aBoolean){
            RSemaphore semaphore = redissonClient.getSemaphore(SeckillConstant.SECKILL_STOCK_PREFIX + randomCode);
            //尝试获取秒杀数量个信号量
            boolean b = semaphore.tryAcquire(num);
            if(b){
                //创建订单号和订单信息发送给MQ
                // 秒杀成功 快速下单 发送消息到 MQ 整个操作时间在 10ms 左右
                Snowflake snowflake = IdUtil.createSnowflake(1, 1);
                log.info("orderSn:{}", snowflake.nextId());
                String orderSn = String.valueOf(snowflake.nextId());
                SeckillOrderTo orderTo = new SeckillOrderTo();
                orderTo.setOrderSn(orderSn);
                orderTo.setMemberId(memberVo.getId());
                orderTo.setNum(num);
                orderTo.setPromotionSessionId(skuTo.getPromotionSessionId());
                orderTo.setSkuId(skuTo.getSkuId());
                orderTo.setSeckillPrice(skuTo.getSeckillPrice());
                rabbitTemplate.convertAndSend(OrderConstant.ORDER_EVENT_EXCHANGE,OrderConstant.ORDER_SECKILL_ROUTE,orderTo);
                long s2 = System.currentTimeMillis();
                log.info("耗时..." + (s2 - s1));
                return orderSn;
            }else{
                log.info("秒杀失败");
                return null;
            }
        }else{
            log.info("已经秒杀过该商品");
            return null;
        }

    }

    /**
     * 保存秒杀关联的商品信息
     * 使用redis的hash结构缓存数据
     * @param records
     */
    private void saveRelationSkus(List<SeckillSessionVo> records) {

        BoundHashOperations<String, Object, Object> skuHashOps = redisTemplate.boundHashOps(SeckillConstant.SECKILL_SKU_CACHE_PREFIX);

        for(SeckillSessionVo session: records){
            List<SeckillSkuVo> seckillSkus = session.getSeckillSkus();
            if(CollectionUtils.isEmpty(seckillSkus)){
                continue;
            }
            //收集本场的所关联的所有skuIds
            Set<Long> skuIds = seckillSkus.stream().map(SeckillSkuVo::getSkuId).collect(Collectors.toSet());
            //如果该场次没有关联商品，那么就不用储存秒杀的商品信息
            if(CollectionUtils.isEmpty(skuIds)){
                continue;
            }

            //远程查询本场的skuInfos
            Result<List<SkuInfoVo>> skuInfoResult = productFeignService.querySkuInfoBatch(skuIds);
            List<SkuInfoVo> skuInfos = new ArrayList<>();

            if(skuInfoResult.getCode() == 0){
                skuInfos = skuInfoResult.getData();
            }

            for (SeckillSkuVo item : seckillSkus) {
                String redisKey = String.format("%s_%s",session.getId(),item.getSkuId());
                Boolean hasKey = skuHashOps.hasKey(redisKey);
                // 如果该场次的商品秒杀清单中没有这个商品
                if(hasKey != null && !hasKey){
                    SeckillSkuTo skuTo = new SeckillSkuTo();
                    List<SkuInfoVo> collect = skuInfos.stream().filter(skuItem -> item.getSkuId().equals(skuItem.getSkuId())).collect(Collectors.toList());
                    //如果当前场次关联的该商品不存在，那么不保存到redis
                    if(CollectionUtils.isEmpty(collect)){
                        continue;
                    }
                    //商品的基本信息
                    skuTo.setSkuInfo(collect.get(0));

                    //商品的秒杀属性
                    BeanUtils.copyProperties(item,skuTo);

                    //商品的开始、结束秒杀时间
                    skuTo.setStartTime(session.getStartTime().getTime());
                    skuTo.setEndTime(session.getEndTime().getTime());

                    //商品的随机码(防止恶意攻击)
                    String token = IdUtil.simpleUUID();
                    skuTo.setRandomCode(token);
                    //保存redis
                    String s = JSONObject.toJSONString(skuTo);
                    skuHashOps.put(redisKey,s);

                    //设置每个商品可以秒杀的数量作为库存信号量
                    RSemaphore semaphore = redissonClient.getSemaphore(SeckillConstant.SECKILL_STOCK_PREFIX + token);
                    semaphore.trySetPermits(item.getSeckillCount());
                }
            }
        }
    }

    /**
     * 向redis保存秒杀场次信息
     * @param records
     */
    private void saveSessions(List<SeckillSessionVo> records){
        records.forEach(session -> {
            long startTime = session.getStartTime().getTime();
            long endTime = session.getEndTime().getTime();
            String sessionKey = SeckillConstant.SESSIONS_CACHE_PREFIX + String.format("%s_%s",startTime,endTime);
            List<SeckillSkuVo> seckillSkus = session.getSeckillSkus();
            Boolean hasKey = redisTemplate.hasKey(sessionKey);
            //如果已经上架过，就不再上架了
            if(hasKey != null && !hasKey && !CollectionUtils.isEmpty(seckillSkus)){
                List<String>  skuIds = seckillSkus.stream().map(item -> {
                    return item.getPromotionSessionId() + "_" + item.getSkuId();
                }).collect(Collectors.toList());
                redisTemplate.opsForList().leftPushAll(sessionKey,skuIds);
            }
        });
    }
}
