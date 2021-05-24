package edu.hubu.mall.client2.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-24
 **/
@Controller
public class BossController {

    @Value("${sso.sever.url}")
    String ssoSeverUrl;

    @GetMapping("/boss.html")
    public String bossPage(HttpSession session, @RequestParam(value = "token",required = false) String token){
        if(!StringUtils.isEmpty(token)){
            session.setAttribute("loginUser","张三");
        }
        if(session.getAttribute("loginUser") == null){
            return "redirect:" + ssoSeverUrl + "?redirect_url=http://client2.com:8082/client/boss.html";
        }

        return "boss";
    }

}
