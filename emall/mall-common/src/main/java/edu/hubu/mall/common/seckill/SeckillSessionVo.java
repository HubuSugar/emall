package edu.hubu.mall.common.seckill;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-07-11
 **/
@Data
public class SeckillSessionVo {

    private Long id;

    /**
     * 场次名称
     */
    private String name;

    /**
     * 场次开始时间
     */
    private Date startTime;

    /**
     * 场次结束时间
     */
    private Date endTime;

    /**
     * 场次状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 本场次要秒杀的商品信息
     */
    private List<SeckillSkuVo> seckillSkus;
}
