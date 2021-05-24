package edu.hubu.mall.client1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-24
 **/
@Controller
public class EmployeeController {

    @Value("${sso.sever.url}")
    String ssoServerUrl;

    /**
     *
     * @param session
     * @param model
     * @param token 只要去sso登录成功就会带上token
     * @return
     */
    @GetMapping("/employee.html")
    public String employee(HttpSession session, Model model, @RequestParam(value = "token",required = false) String token){
        if(!StringUtils.isEmpty(token)){
            //TODO 请求sso-sever根据token获取用户信息，然后保存在session中
            session.setAttribute("loginUser","张三");
        }

        if(session.getAttribute("loginUser") == null){
            return "redirect:" + ssoServerUrl + "?redirect_url=http://client1.com:8081/client/employee.html";
        }
        List<String>  emps = new ArrayList<>();
        emps.addAll(Arrays.asList("张三","李四","王五"));
        model.addAttribute("emps",emps);
        return "employee";
    }

}
