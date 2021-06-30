package edu.hubu.mall.order.vo;

/**
 * @Author: huxiaoge
 * @Date: 2021-06-30
 * @Description: 调用支付宝时封装的参数对象
 **/
import lombok.Data;

@Data
public class PayVo {

    private String out_trade_no; // 商户订单号 必填
    private String subject; // 订单名称 必填
    private String total_amount;  // 付款金额 必填
    private String body; // 商品描述 可空
}
