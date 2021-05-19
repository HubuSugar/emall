package edu.hubu.mall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.dao.AttrGroupDao;
import edu.hubu.mall.entity.AttrGroupEntity;
import edu.hubu.mall.service.AttrGroupService;
import edu.hubu.mall.vo.SpuItemAttrGroupVo;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-05-19
 **/
@Service
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {

    /**
     * 查询当前spuId所有属性的分组信息以及属性对应的属性值
     * @param spuId
     * @param catalogId
     * @return
     */
    @Override
    public List<SpuItemAttrGroupVo> getAttrGroupWithAttrsBySpuId(Long spuId, Long catalogId) {
        return this.getBaseMapper().getAttrGroupWithAttrsBySpuId(spuId,catalogId);
    }
}
