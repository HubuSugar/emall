package edu.hubu.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.common.member.MemberReceiveAddressVo;
import edu.hubu.mall.member.entity.MemberReceiveAddressEntity;

import java.util.List;

/**
 * @Description: 会员地址服务
 * @Author: huxiaoge
 * @Date: 2021-06-04
 **/
public interface MemberAddressService extends IService<MemberReceiveAddressEntity> {
    /**
     * 根据会员id查询会员地址信息
     * @param memberId
     * @return
     */
    List<MemberReceiveAddressVo> queryMemberReceiveAddressList(Long memberId);
}
