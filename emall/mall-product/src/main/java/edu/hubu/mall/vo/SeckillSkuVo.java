package edu.hubu.mall.vo;

import lombok.Data;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-20
 **/
@Data
public class SeckillSkuVo {

    //当前商品秒杀的开始时间
    private Long startTime;

    //当前商品秒杀的结束时间
    private Long endTime;

    //当前商品秒杀的随机码
    private String randomCode;
}
