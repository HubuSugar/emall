package edu.hubu.mall.vo;

import lombok.Data;

import java.util.List;


/**
 * @Description: 封装spu的属性分组对象信息
 * @Author: huxiaoge
 * @Date: 2021-05-19
 **/
@Data
public class SpuItemAttrGroupVo {

    /**
     * 分组名称
     */
    private String groupName;

    /**
     * 每个分组下的属性集合
     */
    private List<AttrVo> attrs;


}
