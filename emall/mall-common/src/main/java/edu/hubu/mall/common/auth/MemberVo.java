package edu.hubu.mall.member.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author: huxiaoge
 * @Date: 2021/5/23
 * @Description:
 **/
@Data
public class MemberVo implements Serializable {

    private Long id;
    private Long levelId;
    private String username;
    private String password;
    private String salt;
    private String nickname;
    private String mobile;
    private String email;
    private Integer gender;
    private String header;
    private Date birth;
    private String city;
    private String job;
    /**
     * 个性签名
     */
    private String sign;
    /**
     * 数据来源
     */
    private Integer sourceType;
    private Integer integration;
    private Integer growth;
    private Integer status;
    private Date createTime;
    private String socialUid;
    private String accessToken;
    private Long expiresIn;

}
