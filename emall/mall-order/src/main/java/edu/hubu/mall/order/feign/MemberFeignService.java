package edu.hubu.mall.order.feign;

import edu.hubu.mall.common.member.MemberReceiveAddressVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-04
 **/
@FeignClient("mall-member")
public interface MemberFeignService {

    @GetMapping("/member/receiveAddress/{memberId}/list")
    List<MemberReceiveAddressVo> queryMemberReceiveAddressList(@PathVariable("memberId") Long memberId);

}
