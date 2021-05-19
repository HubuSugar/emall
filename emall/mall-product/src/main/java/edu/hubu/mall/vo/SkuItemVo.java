package edu.hubu.mall.vo;

import edu.hubu.mall.entity.SkuImgEntity;
import edu.hubu.mall.entity.SkuInfoEntity;
import edu.hubu.mall.entity.SpuInfoDescEntity;
import lombok.Data;

import java.util.List;

/**
 * @Description: 封装商品详情页需要返回的数据
 * @Author: huxiaoge
 * @Date: 2021-05-19
 **/
@Data
public class SkuItemVo {

    /**
     * sku基本信息的获取  pms_sku_info
     */
    private SkuInfoEntity info;

    /**
     * sku是否有库存
     */
    private boolean hasStock = true;

    /**
     * sku的图片信息
     */
    private List<SkuImgEntity> images;

    /**
     * spu的销售属性组合 其中AttrVo(表示每个属性下的所有属性值)
     */
    private List<AttrVo> saleAttrs;

    /**
     * sku对应的spu介绍信息
     */
    private SpuInfoDescEntity spuDesc;

    /**
     * spu的规格参数信息
     */
    private List<SpuItemAttrGroupVo> groupAttrs;




}
