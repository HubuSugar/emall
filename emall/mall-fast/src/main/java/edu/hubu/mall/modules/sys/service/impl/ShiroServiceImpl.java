package edu.hubu.mall.modules.sys.service.impl;

import edu.hubu.mall.common.constant.Constant;
import edu.hubu.mall.modules.sys.dao.MenuDao;
import edu.hubu.mall.modules.sys.dao.UserDao;
import edu.hubu.mall.modules.sys.dao.UserTokenDao;
import edu.hubu.mall.modules.sys.entity.SysMenuEntity;
import edu.hubu.mall.modules.sys.entity.SysUserEntity;
import edu.hubu.mall.modules.sys.entity.SysUserTokenEntity;
import edu.hubu.mall.modules.sys.service.ShiroService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/23
 * @Description:
 **/
@Service
public class ShiroServiceImpl implements ShiroService {

    @Autowired
    private MenuDao menuDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserTokenDao userTokenDao;

    @Override
    public Set<String> getUserPermissions(long userId) {
        List<String> permsList;

        //系统管理员，拥有最高权限
        if(userId == Constant.SUPER_ADMIN_ID){
            List<SysMenuEntity> menuList = menuDao.selectList(null);
            permsList = new ArrayList<>(menuList.size());
            for(SysMenuEntity menu : menuList){
                permsList.add(menu.getPerms());
            }
        }else{
            permsList = userDao.queryAllPerms(userId);
        }
        //用户权限列表
        Set<String> permsSet = new HashSet<>();
        for(String perms : permsList){
            if(StringUtils.isBlank(perms)){
                continue;
            }
            permsSet.addAll(Arrays.asList(perms.trim().split(",")));
        }
        return permsSet;
    }

    @Override
    public SysUserTokenEntity queryByToken(String token) {
        return userTokenDao.queryByToken(token);
    }

    @Override
    public SysUserEntity queryUser(Long userId) {
        return userDao.selectById(userId);
    }
}
