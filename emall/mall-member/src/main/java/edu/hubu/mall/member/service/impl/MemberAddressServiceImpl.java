package edu.hubu.mall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.common.member.MemberReceiveAddressVo;
import edu.hubu.mall.member.dao.MemberAddressDao;
import edu.hubu.mall.member.entity.MemberReceiveAddressEntity;
import edu.hubu.mall.member.service.MemberAddressService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-04
 **/
@Service
public class MemberAddressServiceImpl extends ServiceImpl<MemberAddressDao, MemberReceiveAddressEntity> implements MemberAddressService {

    /**
     * 根据用户id查询用户的收货地址列表
     * @param memberId
     * @return
     */
    @Override
    public List<MemberReceiveAddressVo> queryMemberReceiveAddressList(Long memberId) {
        QueryWrapper<MemberReceiveAddressEntity> queryWrapper = new QueryWrapper<>();
        List<MemberReceiveAddressEntity> address = this.list(queryWrapper.eq("member_id", memberId));
        return address.stream().map(addr -> {
            String s = JSON.toJSONString(addr);
            return JSON.parseObject(s,MemberReceiveAddressVo.class);
        }).collect(Collectors.toList());
    }

    /**
     * 根据id查询地址信息
     * @param addrId
     * @return
     */
    @Override
    public MemberReceiveAddressVo addrInfo(Long addrId) {
        LambdaQueryWrapper<MemberReceiveAddressEntity> queryWrapper = new LambdaQueryWrapper<>();
        MemberReceiveAddressEntity address = this.getOne(queryWrapper.eq(MemberReceiveAddressEntity::getId, addrId));
        String s = JSON.toJSONString(address);
        return JSON.parseObject(s, MemberReceiveAddressVo.class);
    }


}
