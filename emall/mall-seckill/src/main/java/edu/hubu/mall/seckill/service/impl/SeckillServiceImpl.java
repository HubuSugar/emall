package edu.hubu.mall.seckill.service.impl;

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
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

            for (SeckillSkuVo item:session.getSeckillSkus()) {
                SeckillSkuTo skuTo = new SeckillSkuTo();
                List<SkuInfoVo> collect = skuInfos.stream().filter(skuItem -> item.getSkuId().equals(skuItem.getSkuId())).collect(Collectors.toList());
                if(CollectionUtils.isEmpty(collect)){
                    continue;
                }
                //商品的基本信息
                skuTo.setSkuInfo(collect.get(0));
                //商品的秒杀属性
                BeanUtils.copyProperties(item,skuTo);
                //商品的随机码
                String s = JSONObject.toJSONString(skuTo);
                skuHashOps.put(String.valueOf(item.getSkuId()),s);
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
            List<String> skuIds = new ArrayList<>();
            if(!CollectionUtils.isEmpty(seckillSkus)){
                skuIds = seckillSkus.stream().map(item -> {
                    return item.getSkuId().toString();
                }).collect(Collectors.toList());
            }
            redisTemplate.opsForList().leftPushAll(sessionKey,skuIds);
        });
    }
}
