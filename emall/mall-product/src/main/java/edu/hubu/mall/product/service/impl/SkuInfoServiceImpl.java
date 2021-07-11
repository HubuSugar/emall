package edu.hubu.mall.product.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.common.product.SkuInfoVo;
import edu.hubu.mall.product.dao.SkuInfoDao;
import edu.hubu.mall.product.entity.SkuImgEntity;
import edu.hubu.mall.product.entity.SkuInfoEntity;
import edu.hubu.mall.product.entity.SpuInfoDescEntity;
import edu.hubu.mall.product.service.*;
import edu.hubu.mall.product.vo.SkuItemSaleAttrVo;
import edu.hubu.mall.product.vo.SkuItemVo;
import edu.hubu.mall.product.vo.SpuItemAttrGroupVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

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

    @Autowired
    private ThreadPoolExecutor executor;

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
    @Cacheable(value = {"skuDetail"},key = "'product_skuDetail_' + #root.args[0]")
    public SkuItemVo querySkuDetailByIdSynchronized(Long skuId) {
        System.out.println("缓存未命中，查询数据库：skuId -->" + skuId);
        SkuItemVo skuItemVo = new SkuItemVo();
        //1.查询skuInfo基本信息
        SkuInfoEntity skuInfo = getById(skuId);
        skuItemVo.setInfo(skuInfo);

        //2.查询sku对应的图片信息
        List<SkuImgEntity> skuImgs =  skuImgService.querySkuImgsById(skuId);
        skuItemVo.setImages(skuImgs);

        //3.获取spu的销售属性组合
        List<SkuItemSaleAttrVo> saleAttrs =  saleAttrValueService.getSkuItemSaleAttrValuesBySpuId(skuInfo.getSpuId());
        skuItemVo.setSaleAttrs(saleAttrs);

        //4.获取spu的介绍
        SpuInfoDescEntity spuInfoDesc = spuInfoDescService.getById(skuInfo.getSpuId());
        skuItemVo.setSpuDesc(spuInfoDesc);

        //5.获取spu的规格参数信息
        List<SpuItemAttrGroupVo> groupAttrs = attrGroupService.getAttrGroupWithAttrsBySpuId(skuInfo.getSpuId(),skuInfo.getCatalogId());
        skuItemVo.setGroupAttrs(groupAttrs);

        return skuItemVo;
    }

    /**
     * 通过异步编排的方式查询，提高查询效率效率
     * @param skuId
     * @return
     */
    @Cacheable(value = {"skuDetail"},key = "'product_skuDetail_' + #root.args[0]")
    @Override
    public SkuItemVo querySkuDetailByIdAsync(Long skuId) throws ExecutionException, InterruptedException {
        System.out.println("缓存未命中，查询数据库：skuId -->" + skuId);
        SkuItemVo skuItemVo = new SkuItemVo();

        //1.查询skuInfo基本信息
        CompletableFuture<SkuInfoEntity> infoFuture = CompletableFuture.supplyAsync(() -> {
            return getById(skuId);
        },executor);

        //2.查询sku对应的图片信息
        CompletableFuture<List<SkuImgEntity>> imageFuture = CompletableFuture.supplyAsync(() -> {
            return skuImgService.querySkuImgsById(skuId);
        },executor);

        //3.查询所有sku的销售属性信息
        CompletableFuture<List<SkuItemSaleAttrVo>> saleAttrs = infoFuture.thenApplyAsync((skuInfo) -> {
            return saleAttrValueService.getSkuItemSaleAttrValuesBySpuId(skuInfo.getSpuId());
        },executor);

        //4.查询spu的介绍信息
        CompletableFuture<SpuInfoDescEntity> spuInfoFuture = infoFuture.thenApplyAsync(skuInfo -> {
            return spuInfoDescService.getById(skuInfo.getSpuId());
        },executor);

        //5.查询spu的属性分组信息
        CompletableFuture<List<SpuItemAttrGroupVo>> groupAttrs = infoFuture.thenApplyAsync(skuInfo -> {
            return attrGroupService.getAttrGroupWithAttrsBySpuId(skuInfo.getSpuId(), skuInfo.getCatalogId());
        },executor);

        //阻塞等待所有的任务执行完成
        CompletableFuture.allOf(imageFuture,saleAttrs,spuInfoFuture,groupAttrs);

        skuItemVo.setInfo(infoFuture.get());
        skuItemVo.setSaleAttrs(saleAttrs.get());
        skuItemVo.setImages(imageFuture.get());
        skuItemVo.setSpuDesc(spuInfoFuture.get());
        skuItemVo.setGroupAttrs(groupAttrs.get());

        return skuItemVo;
    }

    /**
     * 根据skuId查询skuInfo信息
     * @param skuId
     * @return
     */
    @Override
    public SkuInfoVo querySkuInfoById(Long skuId) {
        SkuInfoEntity skuInfo = this.baseMapper.selectById(skuId);
        String s = JSON.toJSONString(skuInfo);
        return JSON.parseObject(s,new TypeReference<SkuInfoVo>(){});
    }

    /**
     * 根据ids批量查询skuInfo
     * @param ids
     * @return
     */
    @Override
    public List<SkuInfoVo> querySkuInfos(Set<Long> ids) {
        LambdaQueryWrapper<SkuInfoEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SkuInfoEntity::getSkuId, ids);
        List<SkuInfoEntity> list = list(queryWrapper);
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        return list.stream().map(item -> {
            SkuInfoVo skuInfoVo = new SkuInfoVo();
            BeanUtils.copyProperties(item,skuInfoVo);
            return skuInfoVo;
        }).collect(Collectors.toList());

    }
}
