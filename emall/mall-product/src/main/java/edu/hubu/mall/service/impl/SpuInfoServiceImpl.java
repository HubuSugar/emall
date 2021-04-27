package edu.hubu.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.common.to.es.SkuEsModel;
import edu.hubu.mall.common.utils.PageUtil;
import edu.hubu.mall.common.utils.Query;
import edu.hubu.mall.dao.SpuInfoDao;
import edu.hubu.mall.entity.*;
import edu.hubu.mall.feign.WareFeignService;
import edu.hubu.mall.service.*;
import lombok.Data;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/26
 * @Description: spuInfo服务
 **/
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    @Autowired
    private SkuInfoService skuInfoService;

    @Autowired
    private WareFeignService wareFeignService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private BrandService brandService;

    @Autowired
    private ProductAttrValueService productAttrValueService;

    @Autowired
    private AttrService attrService;

    /**
     * 根据条件查询分页信息
     * @param paramMap 查询参数
     * @return
     */
    @Override
    public List<SpuInfoEntity> querySpuInfoPageByCondition(Map<String, Object> paramMap) {
        QueryWrapper<SpuInfoEntity> queryWrapper = new QueryWrapper<>();

        String key = (String) paramMap.get("key");
        if (!StringUtils.isEmpty(key)) {
            queryWrapper.and((wrapper) -> {
                wrapper.eq("id",key).or().like("spu_name",key);
            });
        }

        String status = (String) paramMap.get("status");
        if (!StringUtils.isEmpty(status)) {
            queryWrapper.eq("publish_status",status);
        }

        String brandId = (String) paramMap.get("brandId");
        if (!StringUtils.isEmpty(brandId) && !"0".equalsIgnoreCase(brandId)) {
            queryWrapper.eq("brand_id",brandId);
        }

        String catelogId = (String) paramMap.get("catelogId");
        if (!StringUtils.isEmpty(catelogId) && !"0".equalsIgnoreCase(catelogId)) {
            queryWrapper.eq("catalog_id",catelogId);
        }

        IPage<SpuInfoEntity> page = this.page(new Query<SpuInfoEntity>().getPage(paramMap), queryWrapper);

        PageUtil<SpuInfoEntity> pageUtil = new PageUtil<>(page);
        return pageUtil.getList();
    }

    /**
     * 根据spuId进行商品上架
     * @param spuId
     * @return
     */
    @Override
    public Boolean spuUp(Long spuId) {
        //1.根据spuId查询要上架的skuInfo数据,然后根据skuInfoList封装SkuEsModel
        List<SkuInfoEntity> skuInfoEntities = skuInfoService.querySkuInfoListBySpuId(spuId);

        //批量查询品牌信息和分类信息,这样不用多次查询数据库
        //聚合品牌id
        List<Long> brandIds = skuInfoEntities.stream().map(SkuInfoEntity::getBrandId).distinct().collect(Collectors.toList());
        List<BrandEntity> brandEntities = brandService.queryBrandsByIds(brandIds);
        //聚合分类id
        List<Long> cataLogIds = skuInfoEntities.stream().map(SkuInfoEntity::getCatalogId).distinct().collect(Collectors.toList());
        List<CategoryEntity> categoryEntities = categoryService.queryCategoriesByIds(cataLogIds);

        //查出当前sku的所有可以被用来检索的规格属性
        //(1)根据spuId查询所有的属性
        List<ProductAttrValueEntity> attrValueEntities = productAttrValueService.queryProductAttrValuesBySpuId(spuId);
        //(2)筛选可以被用来检索的属性
        List<Long> attrIds = attrValueEntities.stream().map(ProductAttrValueEntity::getAttrId).distinct().collect(Collectors.toList());
        List<AttrEntity> attrEntities = attrService.queryAttrsByIds(attrIds);
        Set<Long> attrIdSet = attrEntities.stream().map(AttrEntity::getAttrId).collect(Collectors.toSet());

        List<SkuEsModel.Attrs> esSkuAttrList = attrValueEntities.stream().filter(item -> attrIdSet.contains(item.getAttrId())).map(attrValueVo -> {
            SkuEsModel.Attrs skuAttr = new SkuEsModel.Attrs();
            BeanUtils.copyProperties(attrValueVo, skuAttr);
            return skuAttr;
        }).collect(Collectors.toList());

        //批量查询skuIds是否有库存
        List<Long> skuIds = skuInfoEntities.stream().map(SkuInfoEntity::getSkuId).collect(Collectors.toList());
        Map<Long,Boolean> stockMap = null;
        try{
            stockMap = wareFeignService.getSkuHasStock(skuIds);
        }catch (Exception e){
            log.error("远程查询库存服务异常,原因：{}",e);
        }

        Map<Long, Boolean> finalStockMap = stockMap;
        List<SkuEsModel> esModelList = skuInfoEntities.stream().map(skuInfo -> {
            SkuEsModel esModel = new SkuEsModel();
            //设置esModel的skuId,spuId,skuTitle,price,skuImg,saleCount
            esModel.setSkuId(skuInfo.getSkuId());
            esModel.setSpuId(skuInfo.getSpuId());
            esModel.setSkuTitle(skuInfo.getSkuTitle());
            esModel.setSkuPrice(skuInfo.getPrice());
            esModel.setSkuImg(skuInfo.getSkuDefaultImg());
            esModel.setSaleCount(skuInfo.getSaleCount());
            //设置esModel的热度评分,TODO 后续可以通过更复杂的算法进行热度评分
            esModel.setHotScore(0L);
            //设置esModel是否有库存
            if(finalStockMap == null){
                esModel.setHasStock(true);
            }else{
                esModel.setHasStock(finalStockMap.get(skuInfo.getSkuId()));
            }
            //设置品牌和分类信息
            List<BrandEntity> brandList = brandEntities.stream().filter(item -> skuInfo.getBrandId().equals(item.getBrandId())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(brandList)){
                //设置esModel的品牌信息
                esModel.setBrandId(skuInfo.getBrandId());
                esModel.setBrandImg(brandList.get(0).getLogo());
                esModel.setBrandName(brandList.get(0).getName());
            }
            List<CategoryEntity> cataLogList = categoryEntities.stream().filter(item -> skuInfo.getCatalogId().equals(item.getCatId())).collect(Collectors.toList());
            if(CollectionUtils.isNotEmpty(cataLogList)){
                esModel.setCatalogId(skuInfo.getCatalogId());
                esModel.setCatalogName(cataLogList.get(0).getName());
            }
            //设置商品的检索属性
            esModel.setAttrs(esSkuAttrList);

            return esModel;
        }).collect(Collectors.toList());

        //通过搜索服务将商品上架


        return null;
    }
}
