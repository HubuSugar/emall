package edu.hubu.mall.fast.modules.sys.controller;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.fast.modules.sys.entity.SysMenuEntity;
import edu.hubu.mall.fast.modules.sys.service.MenuService;
import edu.hubu.mall.fast.modules.sys.service.ShiroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/23
 * @Description: 菜单入口
 **/
@RestController
@RequestMapping("sys/menu/")
public class SysMenuController extends AbstractController{

    @Autowired
    private MenuService menuService;

    @Autowired
    private ShiroService shiroService;

    @GetMapping("/nav")
    public Result<List<SysMenuEntity>> nav(){
        List<SysMenuEntity> menuList = menuService.queryUserMenuList(getUserId());
        Set<String> permissions = shiroService.getUserPermissions(getUserId());
        Result<List<SysMenuEntity>> result = Result.ok();
        result.setData(menuList);
        result.getExtendMap().put("permissions",permissions);
        return result;
    }

}
