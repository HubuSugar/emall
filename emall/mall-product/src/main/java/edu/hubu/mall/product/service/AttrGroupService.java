package edu.hubu.mall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import edu.hubu.mall.product.entity.AttrGroupEntity;
import edu.hubu.mall.product.vo.SpuItemAttrGroupVo;

import java.util.List;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-19
 **/
public interface AttrGroupService extends IService<AttrGroupEntity> {

    /**
     * 根据spuId和分类Id查询每个spu的属性分组信息
     * @param spuId
     * @param catalogId
     * @return
     */
    List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId);
}
