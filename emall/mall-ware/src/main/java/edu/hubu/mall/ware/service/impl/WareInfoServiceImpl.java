package edu.hubu.mall.ware.service.impl;

import edu.hubu.mall.common.member.MemberReceiveAddressVo;
import edu.hubu.mall.ware.feign.MemberFeignService;
import edu.hubu.mall.ware.service.WareInfoService;
import edu.hubu.mall.common.ware.FareVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/5
 * @Description:
 **/
@Service
public class WareInfoServiceImpl implements WareInfoService {

    @Autowired
    MemberFeignService memberFeignService;

    /**
     * 使用手机号最后一位模拟邮寄运费
     * @return
     */
    @Override
    public FareVo getFare(Long addrId) {
        FareVo fareVo = new FareVo();
        MemberReceiveAddressVo address = memberFeignService.addrInfo(addrId);
        fareVo.setMemberReceiveAddressVo(address);
        if(address != null){
            String fare = address.getPhone() != null ? address.getPhone().substring(address.getPhone().length() - 1) : "10";
            fareVo.setFare(new BigDecimal(fare));
        }
        return fareVo;
    }
}
