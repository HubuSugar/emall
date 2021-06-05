package edu.hubu.mall.auth.feign;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.auth.MemberVo;
import edu.hubu.mall.common.auth.SocialUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @Author: huxiaoge
 * @Date: 2021/5/22
 * @Description:
 **/
@FeignClient("mall-member")
public interface MemberFeignService {

    @PostMapping("/member/login/oauth2")
    Result<MemberVo> oauthLogin(@RequestBody SocialUser socialUser);
}
