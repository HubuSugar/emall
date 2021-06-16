package edu.hubu.mall.ware.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description: 库存锁定实体
 * @Author: huxiaoge
 * @Date: 2021-06-16
 **/
@Data
@TableName("wms_ware_order_task")
public class WareOrderTaskEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long orderId;

    private String orderSn;

    /**
     * 收货人
     */
    private String consignee;
    /**
     * 收货人电话
     */
    private String consigneeTel;
    /**
     * 配送地址
     */
    private String deliveryAddress;
    /**
     * 订单备注
     */
    private String orderComment;
    /**
     * 付款方式
     */
    private Integer paymentWay;
    /**
     * 任务状态
     */
    private Integer taskStatus;
    /**
     * 订单描述
     */
    private String orderBody;

}
