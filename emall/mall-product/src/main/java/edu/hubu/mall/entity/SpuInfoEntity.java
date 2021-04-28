package edu.hubu.mall.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/26
 * @Description:
 **/
@Data
@TableName(value = "pms_spu_info")
public class SpuInfoEntity implements Serializable {

    private static final Long serialVersionUID = 1L;

    @TableId
    private Long id;

    /**
     * spu名称
     */
    private String spuName;

    /**
     * spu描述
     */
    private String spuDescription;

    /**
     * 分类id
     */
    private Long catalogId;

    /**
     * 品牌id
     */
    private Long brandId;

    /**
     * 品牌名称
     */
    @TableField(exist = false)
    private String brandName;

    /**
     * 重量
     */
    private BigDecimal weight;

    /**
     * 发布状态
     */
    private Integer publishStatus;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;

}
