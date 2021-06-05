package edu.hubu.mall.common.ware;

import lombok.Data;

import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/5
 * @Description: 订单项中的每一个商品的锁定结果
 **/
@Data
public class WareLockResultVo {
    private Integer code;  //库存锁定结果
    private List<OrderItemLockResult> lockDetail;   //每个商品的锁定详情

    @Data
    public static class OrderItemLockResult{
        private Long skuId;
        private Integer num;   //锁定件数
        private boolean locked;   //是否锁定成功
    }

}
