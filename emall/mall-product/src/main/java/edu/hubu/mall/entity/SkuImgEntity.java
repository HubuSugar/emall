package edu.hubu.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Description: sku对应的图片实体
 * @Author: huxiaoge
 * @Date: 2021-05-19
 **/
@Data
@TableName("pms_sku_imgs")
public class SkuImgEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * skuId
     */
    private Long skuId;

    /**
     * sku图片路径
     */
    private String imgUrl;

    /**
     * 图片排序
     */
    private Integer imgSort;

    /**
     * 是否是默认图片
     */
    private Integer defaultImg;

}
