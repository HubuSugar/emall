package edu.hubu.mall.fast.modules.sys.service;

import edu.hubu.mall.fast.modules.sys.entity.SysUserEntity;
import edu.hubu.mall.fast.modules.sys.entity.SysUserTokenEntity;

import java.util.Set;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/23
 * @Description:
 **/
public interface ShiroService {

    /**
     * 获取用户权限列表
     */
    Set<String> getUserPermissions(long userId);

    SysUserTokenEntity queryByToken(String token);

    /**
     * 根据用户ID，查询用户
     * @param userId
     */
    SysUserEntity queryUser(Long userId);
}
