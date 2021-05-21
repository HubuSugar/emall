package edu.hubu.mall.auth.webController;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @Description: 登录首页
 * @Author: huxiaoge
 * @Date: 2021-05-20
 **/
@Controller
public class IndexController {

    @GetMapping("/login.html")
    public String loginPage(){

        return "login";
    }

}
