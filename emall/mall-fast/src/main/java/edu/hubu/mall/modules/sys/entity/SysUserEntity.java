package edu.hubu.mall.modules.sys.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/23
 * @Description: 系统管理员用户实体
 **/
@Data
@TableName(value="sys_user")
public class SysUserEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long userId;

    private String username;

    private String password;

    private String salt;

    private String email;

    private String mobile;

    @TableLogic(value = "1",delval = "0")
    private Integer status;

    private Long createUserId;

    private Date createTime;

    /**
     * 角色ID列表
     */
    @TableField(exist=false)
    private List<Long> roleIdList;
}
