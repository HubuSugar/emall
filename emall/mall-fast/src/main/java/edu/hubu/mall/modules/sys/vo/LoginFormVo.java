package edu.hubu.mall.modules.sys.vo;

import lombok.Data;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/23
 * @Description: 登录表单需要提交的信息
 **/
@Data
public class LoginFormVo {
    private String username;
    private String password;
    private String keyCode;
    private String verCodeKey;
}
