package edu.hubu.mall.common.ware;

import edu.hubu.mall.common.member.MemberReceiveAddressVo;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/5
 * @Description: 订单确认页的运费信息
 **/
@Data
public class FareVo {

    private MemberReceiveAddressVo memberReceiveAddressVo;
    private BigDecimal fare;

}
