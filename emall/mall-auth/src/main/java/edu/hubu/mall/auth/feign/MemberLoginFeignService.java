package edu.hubu.mall.auth.feign;

import org.springframework.cloud.openfeign.FeignClient;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-21
 **/
@FeignClient(value = "mall-member")
public interface MemberLoginFeignService {
}
