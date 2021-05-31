package edu.hubu.mall.auth.webController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import edu.hubu.mall.auth.constant.GiteeAuthConstant;
import edu.hubu.mall.auth.feign.MemberFeignService;
import edu.hubu.mall.common.Result;
import edu.hubu.mall.common.auth.SocialUser;
import edu.hubu.mall.common.constant.AuthConstant;
import edu.hubu.mall.member.entity.MemberVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: huxiaoge
 * @Date: 2021/5/22
 * @Description:
 **/
@Controller
public class Auth2Controller {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private MemberFeignService memberFeignService;
    /**
     * gitee登录成功后返回的code
     * @param code
     * @return
     */
    @GetMapping("oauth2/gitee/success")
    public String giteeLogin(@RequestParam("code") String code, HttpSession session, HttpServletResponse servletResponse) throws Exception {

        //设置Http的Header
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        //设置访问参数
        HashMap<String, Object> params = new HashMap<>();
        params.put("grant_type",GiteeAuthConstant.GRANT_TYPE);
        params.put("client_id",GiteeAuthConstant.CLIENT_ID);
        params.put("code",code);
        params.put("redirect_uri",GiteeAuthConstant.REDIRECT_URI_SUCCESS);
        params.put("client_secret",GiteeAuthConstant.CLIENT_SECRET);

        //设置访问的Entity
        HttpEntity httpEntity = new HttpEntity<>(params, headers);
        ResponseEntity<String> response = null;

        //1.使用code换取access_token
        response = restTemplate.exchange(GiteeAuthConstant.GET_ACCESSTOKEN_URL,
                HttpMethod.POST,httpEntity,String.class);

        if(response.getStatusCodeValue() != 200){
          //授权失败，重定向到登录页
           return "redirect:http://auth.emall.com/login.html";
        }

        String accessStr = response.getBody();
        JSONObject json = JSON.parseObject(accessStr);

        SocialUser socialUser = new SocialUser();
        socialUser.setAccessToken(json.getString("access_token"));
        socialUser.setTokenType(json.getString("token_type"));
        socialUser.setExpiresIn(json.getLong("expires_in"));
        socialUser.setRefreshToken(json.getString("refresh_token"));
        socialUser.setScope(json.getString("scope"));
        socialUser.setCreatedAt(json.getLong("created_at"));

        //根据accessToken查询用户信息
        Map<String, Object> userParamMap = new HashMap<>(4);
        userParamMap.put("access_token", socialUser.getAccessToken());
        ResponseEntity<String> userInfo;
        try{
            userInfo = restTemplate.getForEntity(GiteeAuthConstant.GET_USERINFO_URL + socialUser.getAccessToken(),String.class);
        }catch (Exception e){
            throw new Exception("授权登录异常");
        }

        if(userInfo.getStatusCodeValue() != 200){
            //查询用户信息失败
            return "redirect:http://auth.emall.com/login.html";
        }

        String userStr = userInfo.getBody();
        JSONObject userJson = JSON.parseObject(userStr);
        socialUser.setSocialId(userJson.getLong("id"));
        socialUser.setSocialName(userJson.getString("name"));
        socialUser.setAvatarUrl(userJson.getString("avatar_url"));
        //判断用户登录逻辑，是新用户还是已注册用户，用户id始终不变
        Result<MemberVo> result  =  memberFeignService.oauthLogin(socialUser);
        if (result.getSuccess()) {

            //子域名之间的session共享,设置session的时候指定为父域名
            //TODO 当前下发的session都是默认当前域名，所以下发时要放大session的作用域
            //TODO使用的JSON序列化机制
            session.setAttribute(AuthConstant.LOGIN_USER,result.getData());
//            Cookie jsessionid = new Cookie("JSESSIONID", result.getData().toString());
//            servletResponse.addCookie(jsessionid);

            return "redirect:http://emall.com";
        } else {
            return "redirect:http://auth.emall.com/login.html";
        }

    }

}
