package edu.hubu.mall.modules.sys.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.modules.sys.dao.UserDao;
import edu.hubu.mall.modules.sys.entity.SysUserEntity;
import edu.hubu.mall.modules.sys.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/23
 * @Description:
 **/
@Service
public class UserServiceImpl extends ServiceImpl<UserDao, SysUserEntity> implements UserService {

    /**
     * 根据用户名查询用户
     * @param username 用户名
     * @return
     */
    @Override
    public SysUserEntity queryByUsername(String username) {
        LambdaQueryWrapper<SysUserEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysUserEntity::getUsername,username);
        return baseMapper.selectOne(queryWrapper);
    }

    @Override
    public List<Long> queryAllMenuId(Long userId) {
        return baseMapper.queryAllMenuId(userId);
    }
}
