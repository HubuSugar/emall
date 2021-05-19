package edu.hubu.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.common.to.es.SkuEsModel;
import edu.hubu.mall.dao.SkuInfoDao;
import edu.hubu.mall.entity.SkuImgEntity;
import edu.hubu.mall.entity.SkuInfoEntity;
import edu.hubu.mall.entity.SpuInfoDescEntity;
import edu.hubu.mall.service.*;
import edu.hubu.mall.vo.AttrVo;
import edu.hubu.mall.vo.SkuItemVo;
import edu.hubu.mall.vo.SpuItemAttrGroupVo;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description:
 **/
@Service
public class SkuInfoServiceImpl extends ServiceImpl<SkuInfoDao, SkuInfoEntity> implements SkuInfoService {

    @Autowired
    private SkuImgService skuImgService;

    @Autowired
    private SpuInfoDescService spuInfoDescService;

    @Autowired
    private SkuSaleAttrValueService saleAttrValueService;

    @Autowired
    private AttrGroupService attrGroupService;

    /**
     * 根据spuId查询要上架的skuInfoList数据
     * @param spuId spuId
     * @return
     */
    @Override
    public List<SkuInfoEntity> querySkuInfoListBySpuId(Long spuId) {
        LambdaQueryWrapper<SkuInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuInfoEntity::getSpuId,spuId);
        return list(queryWrapper);
    }

    /**
     * 根据skuId查询sku详情
     * @param skuId
     * @return
     */
    @Override
    public SkuItemVo querySkuDetailById(Long skuId) {
        SkuItemVo skuItemVo = new SkuItemVo();
        //1.查询skuInfo基本信息
        SkuInfoEntity skuInfo = getById(skuId);
        skuItemVo.setInfo(skuInfo);

        //2.查询sku对应的图片信息
        List<SkuImgEntity> skuImgs =  skuImgService.querySkuImgsById(skuId);
        skuItemVo.setImages(skuImgs);

        //3.获取spu的销售属性组合
        List<AttrVo> saleAttrs =  saleAttrValueService.getSkuItemSaleAttrValuesBySpuId(skuInfo.getSpuId());
        skuItemVo.setSaleAttrs(saleAttrs);

        //4.获取spu的介绍
        SpuInfoDescEntity spuInfoDesc = spuInfoDescService.getById(skuInfo.getSpuId());
        skuItemVo.setSpuDesc(spuInfoDesc);

        //5.获取spu的规格参数信息
        List<SpuItemAttrGroupVo> groupAttrs = attrGroupService.getAttrGroupWithAttrsBySpuId(skuInfo.getSpuId(),skuInfo.getCatalogId());
        skuItemVo.setGroupAttrs(groupAttrs);

        return skuItemVo;
    }
}
