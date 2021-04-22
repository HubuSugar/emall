package edu.hubu.mall.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.modules.sys.entity.SysUserEntity;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/23
 * @Description:
 **/
public interface UserService extends IService<SysUserEntity> {

    /**
     * 根据用户名查询用户
     * @param username
     * @return
     */
    SysUserEntity queryByUsername(String username);
}
