package edu.hubu.mall.search.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description: 商品属性实体
 **/
@Data

public class AttrEntity {

    private Long attrId;

    private String attrName;

    /**
     * 是否需要检索0-不需要 ；1需要
     */
    private Integer searchType;

    /**
     * 值类型，0 为单个值；1-可以选择多个值
     */
    private Integer valueType;

    /**
     * 属性图标
     */
    private String icon;

    /**
     * 属性可选值列表（用逗号分割）
     */
    private String valueSelect;

    /**
     * 属性类型 0 - 销售属性； 1-基本属性
     */
    private Integer attrType;

    /**
     * 启用状态0-禁用 1-启用
     */
    private Long enable;

    /**
     * 所属分类
     */
    private Long catelogId;

    /**
     * 是否展示在介绍上0-否；1-是，在sku汇总仍然可以调整
     */
    private Integer showDesc;
}
