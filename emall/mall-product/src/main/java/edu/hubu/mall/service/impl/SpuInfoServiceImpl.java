package edu.hubu.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.common.utils.PageUtil;
import edu.hubu.mall.common.utils.Query;
import edu.hubu.mall.dao.SpuInfoDao;
import edu.hubu.mall.entity.SpuInfoEntity;
import edu.hubu.mall.service.SpuInfoService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/26
 * @Description:
 **/
@Service
public class SpuInfoServiceImpl extends ServiceImpl<SpuInfoDao, SpuInfoEntity> implements SpuInfoService {

    /**
     * 根据条件查询分页信息
     * @param paramMap
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
}
