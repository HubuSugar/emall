package edu.hubu.mall.common.constant;

/**
 * @Author: huxiaoge
 * @Date: 2021-07-11
 * @Description: 秒杀服务用到的常量
 **/
public class SeckillConstant {

    /**
     * 场次信息缓存前缀
     */
    public static final String SESSIONS_CACHE_PREFIX = "seckill:sessions:";

    /**
     * 秒杀商品信息的前缀
     */
    public static final String SECKILL_SKU_CACHE_PREFIX = "seckill:skus";

    /**
     * 秒杀商品上架的锁
     */
    public static final String SECKILL_UPLOAD_KEY = "seckill:upload:lock";

    /**
     * 秒杀商品的库存信号量前缀
     */
    public static final String SECKILL_STOCK_PREFIX = "seckill:stock:";

}
