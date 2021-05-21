package edu.hubu.mall.fast.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.fast.modules.sys.entity.SysUserTokenEntity;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/23
 * @Description:
 **/
public interface UserTokenService extends IService<SysUserTokenEntity> {

    String createUserToken(long userId);
}
