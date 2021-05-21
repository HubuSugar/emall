package edu.hubu.mall.fast.modules.sys.controller;

import edu.hubu.mall.fast.modules.sys.entity.SysUserEntity;
import org.apache.shiro.SecurityUtils;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/22
 * @Description: 获取用户id信息或者可以在请求头带上用户的id
 **/
public abstract class AbstractController {


    protected SysUserEntity getUser(){
        return (SysUserEntity) SecurityUtils.getSubject().getPrincipal();
    }

    protected Long getUserId(){
        return getUser().getUserId();
    }
}
