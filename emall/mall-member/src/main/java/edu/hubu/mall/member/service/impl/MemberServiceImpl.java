package edu.hubu.mall.member.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.common.auth.SocialUser;
import edu.hubu.mall.member.dao.MemberDao;
import edu.hubu.mall.member.entity.MemberEntity;
import edu.hubu.mall.member.service.MemberService;
import org.springframework.stereotype.Service;

/**
 * @Author: huxiaoge
 * @Date: 2021/5/23
 * @Description:
 **/
@Service
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    /**
     * 完成社交用户的登录与注册逻辑
     * @param socialUser
     * @return
     */
    @Override
    public MemberEntity login(SocialUser socialUser) {
        Long socialId = socialUser.getSocialId();
        //判断当前用户是否注册后，是走注册逻辑还是登录逻辑
        LambdaQueryWrapper<MemberEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MemberEntity::getSocialUid,socialId);
        MemberEntity memberEntity = this.baseMapper.selectOne(queryWrapper);
        if(null == memberEntity){
            //没有当前用户的信息，现在数据库中保存当前的用户
            memberEntity = new MemberEntity();
            memberEntity.setLevelId(1L);
            memberEntity.setUsername(socialUser.getSocialName());
            memberEntity.setNickname(socialUser.getSocialName());
            memberEntity.setHeader(socialUser.getAvatarUrl());
            memberEntity.setSocialUid(String.valueOf(socialUser.getSocialId()));
            memberEntity.setAccessToken(socialUser.getAccessToken());
            memberEntity.setExpiresIn(socialUser.getExpiresIn());
            this.save(memberEntity);

        }else{
            //如果存在,那么更新acceessToken和过期时间信息
            memberEntity.setAccessToken(socialUser.getAccessToken());
            memberEntity.setExpiresIn(socialUser.getExpiresIn());
            this.updateById(memberEntity);

        }
        return memberEntity;

    }
}
