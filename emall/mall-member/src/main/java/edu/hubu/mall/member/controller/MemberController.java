package edu.hubu.mall.member.controller;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.auth.MemberVo;
import edu.hubu.mall.common.auth.SocialUser;
import edu.hubu.mall.member.entity.MemberEntity;
import edu.hubu.mall.member.service.MemberService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: huxiaoge
 * @Date: 2021/5/23
 * @Description:
 **/
@RestController
@RequestMapping("/member/login")
public class MemberController {

    @Autowired
    private MemberService memberService;


    @PostMapping("/oauth2")
    public Result<MemberVo> oauthLogin(@RequestBody SocialUser socialUser){
        Result<MemberVo> ok = Result.ok();
        MemberEntity memberEntity = memberService.login(socialUser);
        if(memberEntity == null){
            return Result.error(1,"登录失败");
        }
        MemberVo member = new MemberVo();
        BeanUtils.copyProperties(memberEntity,member);
        ok.setData(member);
        return ok;
    }

}
