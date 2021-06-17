package edu.hubu.mall.common.ware;

import lombok.Data;

/**
 * @Description: 库存工作单详情vo
 * @Author: huxiaoge
 * @Date: 2021-06-17
 **/
@Data
public class WareTaskDetailVo {

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
