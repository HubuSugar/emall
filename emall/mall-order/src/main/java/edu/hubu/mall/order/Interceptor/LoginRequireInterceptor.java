package edu.hubu.mall.order.Interceptor;

import edu.hubu.mall.common.auth.HostHolder;
import edu.hubu.mall.common.constant.AuthConstant;
import edu.hubu.mall.member.entity.MemberVo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Description: 用户登录的拦截器
 * @Author: huxiaoge
 * @Date: 2021-06-04
 **/
@Component
public class LoginRequireInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();
        MemberVo user = (MemberVo)session.getAttribute(AuthConstant.LOGIN_USER);
        if(user != null){
            loginUser.set(user);
            return true;
        }else{
            //没有登录，跳转到登录页
            session.setAttribute("msg","请先登录");
            response.sendRedirect("http://auth.emall.com/login.html");
            return false;
        }
    }
}
