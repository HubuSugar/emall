package edu.hubu.mall.ware.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-16
 **/
@Data
@TableName("wms_ware_order_task_detail")
public class WareOrderTaskDetailEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long skuId;

    private String skuName;
    /**
     * 购买数量
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;
    /**
     * 仓库id
     */
    private Long wareId;
    /**
     * 锁定状态1-已锁定  2-已解锁  3-扣减
     */
    private Integer lockStatus;

}
