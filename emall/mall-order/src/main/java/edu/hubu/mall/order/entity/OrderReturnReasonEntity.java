package edu.hubu.mall.order.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @Description: 退货原因实体
 * @Author: huxiaoge
 * @Date: 2021-06-03
 **/
@Data
public class OrderReturnReasonEntity implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;
    private Long orderId;
    private String name;
    private Integer sort;
    private Integer Status;
    private Date createTime;

}
