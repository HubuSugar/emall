package edu.hubu.mall.member.feign;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-04
 **/
@FeignClient("mall-order")
public interface OrderFeignService {
}
