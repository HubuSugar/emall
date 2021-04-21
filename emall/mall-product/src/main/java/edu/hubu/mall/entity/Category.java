package edu.hubu.mall.entity;


import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/20
 * @Description: 三级分类实体
 **/
@Data
@TableName(value = "pms_category")
public class Category implements Serializable {

    private static final Long serialVersionUID = 1L;
    /**
     * 分类主键，采用自增id
     */
    @TableId
    private Long catId;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 父类id
     */
    private Long parentCid;

    /**
     * 分类层级
     */
    private Integer catLevel;

    /**
     * 是否显示[0-不显示，1显示]
     */
    @TableLogic(value = "1",delval = "0")
    private Integer showStatus;

    /**
     * 分类排序
     */
    private Integer sort;

    /**
     * 分类图标
     */
    private String icon;

    /**
     * 每个分类的单位(件，个)
     */
    private String productUnit;

    /**
     * 每个分类的数量
     */
    private Integer productCount;

}
