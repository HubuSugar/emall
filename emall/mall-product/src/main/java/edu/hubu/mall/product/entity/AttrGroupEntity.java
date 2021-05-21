package edu.hubu.mall.product.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @Description: 商品属性分组实体(一个分类有多少个属性组)
 * @Author: huxiaoge
 * @Date: 2021-05-19
 **/
@Data
@TableName("pms_attr_group")
public class AttrGroupEntity {

    /**
     * 分组id
     */
    @TableId(type = IdType.INPUT)
    private Long attrGroupId;

    /**
     * 分组名称
     */
    private String attrGroupName;

    /**
     * 商品分类id
     */
    private Integer catalogId;

    /**
     * 分组排序
     */
    private Integer sort;

    /**
     * 属性图标
     */
    private String icon;

    /**
     * 属性介绍描述
     */
    private String descript;

}
