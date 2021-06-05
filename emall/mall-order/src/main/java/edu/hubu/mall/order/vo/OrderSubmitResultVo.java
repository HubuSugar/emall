package edu.hubu.mall.order.vo;

import edu.hubu.mall.order.entity.OrderEntity;
import lombok.Data;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/5
 * @Description: 封装订单提交成功的返回数据
 **/
@Data
public class OrderSubmitResultVo {

    private Integer code;   //返回的状态码
    private OrderEntity order;   //订单信息

}
