package edu.hubu.mall.search.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description: 商品品牌实体
 **/
@Data
public class BrandEntity implements Serializable {

    private Long brandId;

    private String name;

    /**
     * 品牌标志
     */
    private String logo;

    /**
     * 描述
     */
    private String descript;

    /**
     * 展示状态
     */
    @TableLogic(value = "1",delval = "0")
    private Integer showStatus;

    /**
     * 品牌首字母
     */
    private String firstLetter;

    /**se
     * 排序
     */
    private Integer sort;
}
