package edu.hubu.mall.member.controller;

import edu.hubu.mall.common.member.MemberReceiveAddressVo;
import edu.hubu.mall.member.service.MemberAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Description: 用户收货地址
 * @Author: huxiaoge
 * @Date: 2021-06-04
 **/
@RestController
@RequestMapping("/member")
public class MemberReceiveAddrController {

    @Autowired
    MemberAddressService memberAddressService;

    /**
     * 根据会员id查询会员的收货地址
     * @param memberId
     * @return
     */
    @GetMapping("/receiveAddress/{memberId}/list")
    public List<MemberReceiveAddressVo> queryMemberReceiveAddressList(@PathVariable("memberId") Long memberId){
        return  memberAddressService.queryMemberReceiveAddressList(memberId);
    }

}
