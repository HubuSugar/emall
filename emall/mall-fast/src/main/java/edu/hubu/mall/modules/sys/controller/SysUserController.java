package edu.hubu.mall.modules.sys.controller;

import edu.hubu.mall.common.Result;
import edu.hubu.mall.modules.sys.entity.SysUserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/23
 * @Description:
 **/
@RestController
@RequestMapping("sys/user/")
@Slf4j
public class SysUserController extends AbstractController{


    @GetMapping("info")
    public Result<SysUserEntity> getUserInfo(){
        Result<SysUserEntity> result = Result.ok();
        SysUserEntity loginUser = getUser();
        result.setData(loginUser);
        return result;
    }
}
