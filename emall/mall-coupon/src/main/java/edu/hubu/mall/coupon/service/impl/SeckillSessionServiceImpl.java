package edu.hubu.mall.coupon.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.common.constant.Constant;
import edu.hubu.mall.common.seckill.SeckillSessionVo;
import edu.hubu.mall.common.seckill.SeckillSkuVo;
import edu.hubu.mall.common.utils.PageUtil;
import edu.hubu.mall.common.utils.Query;
import edu.hubu.mall.common.utils.QueryParam;
import edu.hubu.mall.coupon.dao.SeckillSessionDao;
import edu.hubu.mall.coupon.entity.SeckillSessionEntity;
import edu.hubu.mall.coupon.entity.SeckillSkuEntity;
import edu.hubu.mall.coupon.service.SeckillSessionService;
import edu.hubu.mall.coupon.service.SeckillSkuService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-07-05
 **/
@Service
public class SeckillSessionServiceImpl extends ServiceImpl<SeckillSessionDao, SeckillSessionEntity> implements SeckillSessionService {

    @Autowired
    SeckillSkuService seckillSkuService;

    /**
     * 分页查询秒杀场次的信息
     * @param queryParam
     * @return
     */
    @Override
    public PageUtil<SeckillSessionEntity> queryPage(QueryParam queryParam) {

        LambdaQueryWrapper<SeckillSessionEntity> queryWrapper = new LambdaQueryWrapper<>();
        if(queryParam.getKey() != null){
            queryWrapper.eq(SeckillSessionEntity::getId,queryParam.getKey());
        }
        if(!StringUtils.isEmpty(queryParam.getName())){
            queryWrapper.eq(SeckillSessionEntity::getName,queryParam.getName());
        }
        HashMap<String, Object> pageMap = new HashMap<>();
        pageMap.put(Constant.PAGE_NO,queryParam.getPageNo());
        pageMap.put(Constant.PAGE_SIZE,queryParam.getPageSize());

        IPage<SeckillSessionEntity> page = this.page(new Query<SeckillSessionEntity>().getPage(pageMap),queryWrapper);
        return new PageUtil<>(page);
    }

    /**
     * 保存或者修改场次信息
     * @param seckillSession
     * @return
     */
    @Override
    public boolean saveOrUpdateSeckillSession(SeckillSessionEntity seckillSession) {
        //新增操作
        if(seckillSession.getId() == null){
            this.save(seckillSession);
        }else{
            SeckillSessionEntity old = this.getById(seckillSession.getId());
            if(old == null){
                return false;
            }
            seckillSession.setId(old.getId());
            this.updateById(seckillSession);
        }
        return true;
    }

    /**
     * 获取近三天要秒杀的商品信息
     * @return
     */
    @Override
    public List<SeckillSessionVo> getLatest3DaysSessions() {
        LambdaQueryWrapper<SeckillSessionEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.between(SeckillSessionEntity::getStartTime,startTime(),endTime());
        queryWrapper.eq(SeckillSessionEntity::getStatus,1);
        List<SeckillSessionEntity> list = this.list(queryWrapper);
        //没有要参与秒杀的场次
        if(CollectionUtils.isEmpty(list)){
            return null;
        }
        Set<Long> sessionIds = list.stream().map(SeckillSessionEntity::getId).collect(Collectors.toSet());
        //找到所有场次关联的商品
        List<SeckillSkuEntity> seckillSkus = seckillSkuService.querySeckillSkuByIds(sessionIds);

        return list.stream().map(session -> {
            SeckillSessionVo seckillSessionVo = new SeckillSessionVo();
            BeanUtils.copyProperties(session,seckillSessionVo);
            if(!CollectionUtils.isEmpty(seckillSkus)){
                List<SeckillSkuVo> collect = seckillSkus.stream().filter(sku -> session.getId().equals(sku.getPromotionSessionId())).map(item -> {
                    SeckillSkuVo seckillSkuVo = new SeckillSkuVo();
                    BeanUtils.copyProperties(item, seckillSkuVo);
                    return seckillSkuVo;
                }).collect(Collectors.toList());
                seckillSessionVo.setSeckillSkus(collect);
            }
            return seckillSessionVo;
        }).collect(Collectors.toList());
    }


    /**
     * 获取近三天的开始时间 2021-07-11 00:00:00
     */
    private String startTime(){
        LocalDateTime start = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
        return start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
    /**
     * 获取近三天的结束时间 2021-07-13 23：59:59
     */
    private String endTime(){
        LocalDate now = LocalDate.now();
        LocalDate localDate = now.plusDays(2);
        LocalDateTime of = LocalDateTime.of(localDate, LocalTime.MAX);
        return of.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
