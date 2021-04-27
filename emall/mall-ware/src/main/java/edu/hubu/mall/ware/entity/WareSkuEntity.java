package edu.hubu.mall.ware.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
@Data
@TableName("wms_ware_sku")
public class WareSkuEntity implements Serializable {

    private static final Long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long wareId;

    private Long skuId;

    private String skuName;

    /**
     * 库存量
     */
    private Integer stock;

    /**
     * 锁定库存
     */
    private Integer stockLocked;

}
