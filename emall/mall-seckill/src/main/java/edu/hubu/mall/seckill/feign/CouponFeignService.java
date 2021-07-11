package edu.hubu.mall.seckill.feign;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.seckill.SeckillSessionVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-07-11
 **/
@FeignClient("mall-coupon")
public interface CouponFeignService {

    @GetMapping("/coupon/seckillsession/latest3DaySession")
    Result<List<SeckillSessionVo>> getLatest3DaysSessions();
}
