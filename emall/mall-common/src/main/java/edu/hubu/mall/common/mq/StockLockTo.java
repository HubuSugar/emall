package edu.hubu.mall.common.mq;

import edu.hubu.mall.common.ware.WareTaskDetailVo;
import lombok.Data;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-06-17
 **/
@Data
public class StockLockTo {

    /**
     * 哪一个工作单
     */
    private Long taskId;

    /**
     * 工作单详情实体
     */
    private WareTaskDetailVo taskDetail;

}
