package edu.hubu.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.common.auth.MemberVo;
import edu.hubu.mall.common.auth.SocialUser;
import edu.hubu.mall.member.entity.MemberEntity;

/**
 * @Author: huxiaoge
 * @Date: 2021/5/23
 * @Description: 用户服务
 **/
public interface MemberService extends IService<MemberEntity> {

    /**
     * 完成用户的登录注册逻辑
     */
    MemberEntity login(SocialUser socialUser);

}
