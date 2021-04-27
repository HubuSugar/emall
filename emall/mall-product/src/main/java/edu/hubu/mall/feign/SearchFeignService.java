package edu.hubu.mall.feign;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
@FeignClient("mall-search")
public interface SearchFeignService {
}
