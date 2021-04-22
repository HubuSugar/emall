package edu.hubu.mall.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/23
 * @Description: 系统管理员用户实体
 **/
@Data
@TableName(value="sys_user")
public class SysUserEntity {

    @TableId(type = IdType.AUTO)
    private Long userId;

    private String username;

    private String password;

    private String salt;

    private String email;

    private String mobile;

    @TableLogic(value = "1",delval = "0")
    private String status;

    private Long createUserId;

    private Date createTime;
}
