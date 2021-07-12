package edu.hubu.mall.seckill.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.constant.SeckillConstant;
import edu.hubu.mall.common.product.SkuInfoVo;
import edu.hubu.mall.common.seckill.SeckillSessionVo;
import edu.hubu.mall.common.seckill.SeckillSkuVo;
import edu.hubu.mall.seckill.feign.CouponFeignService;
import edu.hubu.mall.seckill.feign.ProductFeignService;
import edu.hubu.mall.seckill.service.SeckillService;
import edu.hubu.mall.seckill.to.SeckillSkuTo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
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
