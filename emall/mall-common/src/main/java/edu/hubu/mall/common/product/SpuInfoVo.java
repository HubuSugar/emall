package edu.hubu.mall.common.product;

import com.baomidou.mybatisplus.annotation.TableField;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/5
 * @Description: 封装spuInfo数据
 **/
@Data
public class SpuInfoVo {

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
