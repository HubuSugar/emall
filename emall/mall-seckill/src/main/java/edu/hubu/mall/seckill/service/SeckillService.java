package edu.hubu.mall.seckill.service;

import edu.hubu.mall.common.seckill.SeckillSkuVo;
import edu.hubu.mall.seckill.to.SeckillSkuTo;

import java.util.List;

/**
 * @Description: 秒杀的服务
 * @Author: huxiaoge
 * @Date: 2021-07-11
 **/
public interface SeckillService {

    void uploadSeckillSkuLatest3Days();

    List<SeckillSkuTo> getCurrentSeckillSkus();

    SeckillSkuVo querySeckillSkuInfo(Long skuId);

    String kill(String killId, String key, Integer num);
}
