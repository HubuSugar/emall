package edu.hubu.mall.common.ware;

import edu.hubu.mall.common.order.OrderItemVo;
import lombok.Data;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/5
 * @Description: 封装锁定库存的数据
 **/
@Data
public class WareSkuLockVo {

    /**
     * 哪一个订单号
     */
    private String OrderSn;
    /**
     * 需要锁库存的商品信息
     */
    private List<OrderItemVo> locks;

}
