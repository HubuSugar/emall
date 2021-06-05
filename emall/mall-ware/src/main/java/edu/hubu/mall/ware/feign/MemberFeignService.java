package edu.hubu.mall.ware.feign;

import edu.hubu.mall.common.auth.MemberVo;
import edu.hubu.mall.common.member.MemberReceiveAddressVo;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/5
 * @Description:
 **/
@FeignClient("mall-member")
public interface MemberFeignService {

    @GetMapping("/member/{addrId}/info")
    MemberReceiveAddressVo addrInfo(@PathVariable("addrId") Long addrId);

}
