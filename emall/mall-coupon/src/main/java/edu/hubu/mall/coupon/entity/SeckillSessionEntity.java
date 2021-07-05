package edu.hubu.mall.coupon.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.annotation.sql.DataSourceDefinition;
import java.util.Date;

/**
 * @Description: 秒杀场次实体
 * @Author: huxiaoge
 * @Date: 2021-07-05
 **/
@Data
@TableName("sms_seckill_session")
public class SeckillSessionEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 场次名称
     */
    private String name;

    /**
     * 场次开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-mm HH:mm:ss")
    private Date startTime;

    /**
     * 场次结束时间
     */
    @JsonFormat(pattern = "yyyy-MM-mm HH:mm:ss")
    private Date endTime;

    /**
     * 场次状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;
}
