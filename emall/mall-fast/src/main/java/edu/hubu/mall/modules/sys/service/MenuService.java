package edu.hubu.mall.modules.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.modules.sys.entity.SysMenuEntity;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/23
 * @Description:
 **/
public interface MenuService extends IService<SysMenuEntity> {

    /**
     * 根据父菜单Id查询子菜单
     */
    List<SysMenuEntity> queryMenuListByParentId(Long parentId,List<Long> menuIdList);
    /**
     * 根据父菜单Id查询子菜单
     */
    List<SysMenuEntity> queryMenuListByParentId(Long parentId);

    /**
     * 根据用户id获取菜单列表
     * @param userId
     * @return
     */
    List<SysMenuEntity> queryUserMenuList(Long userId);
}
