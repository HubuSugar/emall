package edu.hubu.mall.common.auth;

import lombok.Data;

/**
 * @Description: 临时用户和登录用户的对应关系
 * @Author: huxiaoge
 * @Date: 2021-05-31
 **/
@Data
public class HostHolder {
    private Long userId;
    private String userKey;
    /**
     * 判断是否是临时用户
     */
    private boolean tempUser;
}
