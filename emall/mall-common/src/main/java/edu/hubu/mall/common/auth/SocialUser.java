package edu.hubu.mall.common.auth;

import lombok.Data;

/**
 * @Author: huxiaoge
 * @Date: 2021/5/22
 * @Description: 社交用户登录实体
 **/
@Data
public class SocialUser {

    /**
     * 每一次的有效token
      */
     private String accessToken;
     private String tokenType;
     /**
      * accessToken的有效期
      */
     private Long expiresIn;
     private String refreshToken;
     private String scope;
     private Long createdAt;
     /**
      * 社交用户的id
      */
     private Long socialId;
     /**
      * 社交用户的名称
      */
     private String socialName;
     /**
      * 头像地址
      */
     private String avatarUrl;

}
