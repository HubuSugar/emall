package edu.hubu.mall.member.interceptor;

import edu.hubu.mall.common.auth.MemberVo;
import edu.hubu.mall.common.constant.AuthConstant;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
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

        AntPathMatcher antPathMatcher = new AntPathMatcher();
        boolean match = antPathMatcher.match("/member/**", request.getRequestURI());
        if(match){
            //如果其他外部请求查询订单信息，直接放行
            return true;
        }

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
