package edu.hubu.mall.ssoserver.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-24
 **/
@Controller
public class LoginController {

    @Autowired
    StringRedisTemplate redisTemplate;

    @GetMapping("/login.html")
    public String loginPage(Model model,
                            @RequestParam(value="redirect_url",required = false) String url,
                            @CookieValue(value = "sso_token",required = false) String ssoToken){
        if(!StringUtils.isEmpty(ssoToken)){
            //说明之前有其他系统登录过，直接重定向到当前正在访问的页面
            return "redirect:"+url + "?token=" + ssoToken;
        }
        model.addAttribute("url",url);
        return "login";
    }

    @GetMapping("/doLogin")
    public String doLogin(String username, String password, String url, HttpServletResponse response){
        if(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)){
            //登录成功后将用户的信息保存在redis中
            String uuid = UUID.randomUUID().toString().replace("-", "");
            redisTemplate.opsForValue().set(uuid,username);

            //下发一个cookie在服务器留下一个记号
            Cookie sso_token = new Cookie("sso_token", uuid);
            response.addCookie(sso_token);

            //重定向时带上令牌
            return "redirect:"+url + "?token=" + uuid;
        }

        return "login";
    }
}
