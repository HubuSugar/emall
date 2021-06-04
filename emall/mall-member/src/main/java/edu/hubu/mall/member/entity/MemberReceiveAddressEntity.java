package edu.hubu.mall.member.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description: 收货信息实体
 * @Author: huxiaoge
 * @Date: 2021-06-04
 **/
@Data
@TableName("ums_member_receive_address")
public class MemberReceiveAddressEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long memberId;

    private String name;

    private String phone;

    private String postCode;

    /**
     * 地址信息
     */
    private String province;

    private String city;

    private String region;

    private String detailAddress;

    private String areacode;

    private Integer defaultStatus;
}
