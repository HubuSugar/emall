package edu.hubu.mall.auth.constant;

/**
 * @Author: huxiaoge
 * @Date: 2021/5/22
 * @Description: gitee社交常量
 **/
public class GiteeAuthConstant {

    public static final String GET_ACCESSTOKEN_URL = "https://gitee.com/oauth/token";

    public static final String GET_USERINFO_URL = "https://gitee.com/api/v5/user?access_token=";

    public static final String CLIENT_ID = "052c5f844af58b736024b807bc7125ded076ccee23b9a549df3504f097ecba70";

    public static final String CLIENT_SECRET = "ed465eb2f0faa25494bf69fe3cbf432885ecd7d7f73a272fe78a50dedf6db12e";

    public static final String REDIRECT_URI_SUCCESS = "http://auth.emall.com/oauth2/gitee/success";

    public static final String GRANT_TYPE = "authorization_code";
}
