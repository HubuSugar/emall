package edu.hubu.mall.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @Description: 自定义feign 的请求头信息配置，防止丢失请求头（订单服务，调用购物车服务时，会丢失用户的登录状态信息）
 * RequestContextHolder 表示请求的上下文信息
 * 异步模式下feign会丢失上下文
 * @Author: huxiaoge
 * @Date: 2021-06-04
 **/
@Configuration
public class MemberFeignConfig implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        /**
         * 同步的是当前线程的请求头数据
         */
        //获取到当前请求头的数据,
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if(requestAttributes != null){
            //原始请求
            HttpServletRequest request = requestAttributes.getRequest();
            if(request != null){
                //原始请求的请求头
                String cookie = request.getHeader("Cookie");
                //给feign构造的请求模板加上请求头
                requestTemplate.header("Cookie",cookie);
            }
        }
    }
}
