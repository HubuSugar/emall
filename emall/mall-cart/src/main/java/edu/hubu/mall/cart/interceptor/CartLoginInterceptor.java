package edu.hubu.mall.cart.interceptor;

import edu.hubu.mall.common.auth.HostHolder;
import edu.hubu.mall.common.constant.AuthConstant;
import edu.hubu.mall.common.constant.CartConstant;
import edu.hubu.mall.member.entity.MemberVo;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-31
 **/
@Component
public class CartLoginInterceptor implements HandlerInterceptor {

    public static ThreadLocal<HostHolder> threadLocal = new ThreadLocal<>();

    /**
     * 在目标方法执行之前拦截
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HostHolder hostHolder = new HostHolder();
        HttpSession session = request.getSession();
        MemberVo member =(MemberVo) session.getAttribute(AuthConstant.LOGIN_USER);
        if(member != null){

        }
        //用户没有登录
        Cookie[] cookies = request.getCookies();
        if(cookies != null && cookies.length > 0){
            for (Cookie cookie:cookies) {
                String name = cookie.getName();
                if(CartConstant.TEMP_USER_COOKIE_NAME.equalsIgnoreCase(name)){
                    //找到了临时用户的键
                    hostHolder.setUserKey(cookie.getValue());
                }
            }
        }
        threadLocal.set(hostHolder);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
