package edu.hubu.mall.common.member;

import lombok.Data;

/**
 * @Description: 用户收货地址
 * @Author: huxiaoge
 * @Date: 2021-06-04
 **/
@Data
public class MemberReceiveAddressVo {

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

    private String areaCode;

    private Integer defaultStatus;

}
