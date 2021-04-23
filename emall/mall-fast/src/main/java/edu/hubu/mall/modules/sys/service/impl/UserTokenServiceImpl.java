package edu.hubu.mall.modules.sys.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.modules.sys.dao.UserTokenDao;
import edu.hubu.mall.modules.sys.entity.SysUserTokenEntity;
import edu.hubu.mall.modules.sys.service.UserTokenService;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/23
 * @Description:
 **/
@Service
public class UserTokenServiceImpl extends ServiceImpl<UserTokenDao, SysUserTokenEntity> implements UserTokenService {
    //12个小时后过期 毫秒为单位
    private static final int EXPIRE = 1000 * 3600 * 12;
    /**
     * 未登录用户生成token
     * @param userId
     * @return
     */
    @Override
    public String createUserToken(long userId) {
        //生成一个md5加密的随机码
        String token = IdUtil.simpleUUID();
        token = DigestUtil.md5Hex(token);
        //当前时间
        Date now = new Date();
        //过期时间
        Date expireTime = new Date(now.getTime() + EXPIRE);
        //判断是否为当前用户生成了token
        SysUserTokenEntity userToken = this.getById(userId);
        if(null == userToken){
            userToken = new SysUserTokenEntity();
            userToken.setUserId(userId);
            userToken.setToken(token);
            userToken.setExpireTime(expireTime);
            userToken.setUpdateTime(now);
            this.save(userToken);
        }else{
            //给token延长时间
            userToken.setToken(token);
            userToken.setExpireTime(expireTime);
            userToken.setUpdateTime(now);
            this.updateById(userToken);
        }

        return userToken.getToken();
    }
}
