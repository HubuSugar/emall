package edu.hubu.mall.fast.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.fast.modules.sys.entity.SysUserEntity;

import java.util.List;

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

    /**
     * 查询用户的所有菜单ID
     */
    List<Long> queryAllMenuId(Long userId);
}
