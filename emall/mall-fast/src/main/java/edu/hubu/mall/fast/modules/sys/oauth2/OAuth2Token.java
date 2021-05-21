package edu.hubu.mall.fast.modules.sys.oauth2;

import org.apache.shiro.authc.AuthenticationToken;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/23
 * @Description:
 **/
public class OAuth2Token implements AuthenticationToken {
    private String token;

    public OAuth2Token(String token){
        this.token = token;
    }

    @Override
    public String getPrincipal() {
        return token;
    }

    @Override
    public Object getCredentials() {
        return token;
    }
}
