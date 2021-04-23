package edu.hubu.mall.modules.sys.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.common.Constant;
import edu.hubu.mall.modules.sys.dao.MenuDao;
import edu.hubu.mall.modules.sys.entity.SysMenuEntity;
import edu.hubu.mall.modules.sys.service.MenuService;
import edu.hubu.mall.modules.sys.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/23
 * @Description: 菜单服务
 **/
@Service
public class MenuServiceImpl extends ServiceImpl<MenuDao, SysMenuEntity> implements MenuService {

    @Autowired
    private UserService userService;
    /**
     * 根据父菜单id查询子菜单
     * @param parentId 父菜单Id
     * @return 子菜单列表
     */
    @Override
    public List<SysMenuEntity> queryMenuListByParentId(Long parentId,List<Long> menuIdList) {
        List<SysMenuEntity> menuList = queryMenuListByParentId(parentId);
        if(menuIdList == null){
            return menuList;
        }

        List<SysMenuEntity> userMenuList = new ArrayList<>();
        for(SysMenuEntity menu : menuList){
            if(menuIdList.contains(menu.getMenuId())){
                userMenuList.add(menu);
            }
        }
        return userMenuList;
    }
    /**
     * 根据父菜单id查询子菜单
     * @param parentId 父菜单Id
     * @return 子菜单列表
     */
    @Override
    public List<SysMenuEntity> queryMenuListByParentId(Long parentId) {
        return baseMapper.queryMenuListByParentId(parentId);
    }

    /**
     * 根据用户id获取菜单列表，如果是管理员查询所有菜单列表，普通用户根据id查询
     * @param userId 用户Id
     * @return 用户菜单列表
     */
    @Override
    public List<SysMenuEntity> queryUserMenuList(Long userId) {
        if(userId == Constant.SUPER_ADMIN_ID){
            return getAllMenuList(null);
        }
        //用户菜单列表
        List<Long> menuIdList = userService.queryAllMenuId(userId);
        return getAllMenuList(menuIdList);
    }

    /**
     * 获取所有菜单列表
     */
    private List<SysMenuEntity> getAllMenuList(List<Long> menuIdList){
        //查询根菜单列表
        List<SysMenuEntity> menuList = queryMenuListByParentId(0L, menuIdList);
        //递归获取子菜单
        getMenuTreeList(menuList, menuIdList);

        return menuList;
    }

    /**
     * 递归
     */
    private List<SysMenuEntity> getMenuTreeList(List<SysMenuEntity> menuList, List<Long> menuIdList){
        List<SysMenuEntity> subMenuList = new ArrayList<SysMenuEntity>();

        for(SysMenuEntity entity : menuList){
            //目录
            if(entity.getType() == Constant.MenuType.CATALOG.getValue()){
                entity.setList(getMenuTreeList(queryMenuListByParentId(entity.getMenuId(), menuIdList), menuIdList));
            }
            subMenuList.add(entity);
        }

        return subMenuList;
    }

}
