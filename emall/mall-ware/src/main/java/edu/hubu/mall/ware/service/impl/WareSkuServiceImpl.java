package edu.hubu.mall.ware.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.common.constant.WareConstant;
import edu.hubu.mall.common.exception.NoStockException;
import edu.hubu.mall.common.mq.StockLockTo;
import edu.hubu.mall.common.order.OrderItemVo;
import edu.hubu.mall.common.order.OrderVo;
import edu.hubu.mall.common.ware.WareSkuLockVo;
import edu.hubu.mall.common.ware.WareTaskDetailVo;
import edu.hubu.mall.ware.dao.WareSkuDao;
import edu.hubu.mall.ware.entity.WareOrderTaskDetailEntity;
import edu.hubu.mall.ware.entity.WareOrderTaskEntity;
import edu.hubu.mall.ware.entity.WareSkuEntity;
import edu.hubu.mall.ware.feign.OrderFeignService;
import edu.hubu.mall.ware.service.WareOrderTaskDetailService;
import edu.hubu.mall.ware.service.WareOrderTaskService;
import edu.hubu.mall.ware.service.WareSkuService;
import edu.hubu.mall.ware.vo.WareSkuStockVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description: sku库存服务
 **/
@Service
@Slf4j
public class WareSkuServiceImpl extends ServiceImpl<WareSkuDao, WareSkuEntity> implements WareSkuService {

    @Autowired
    private WareOrderTaskService orderTaskService;

    @Autowired
    private WareOrderTaskDetailService taskDetailService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    OrderFeignService orderFeignService;

    /**
     * 根据skuIds查询库存数量
     * @param skuIds
     * @return
     */
    @Override
    public Map<Long, Boolean> getSkuHasStockBySkuIds(List<Long> skuIds) {
        List<WareSkuStockVo> wareSkuStockVos = baseMapper.queryStockCountBySkuIds(skuIds);
        return wareSkuStockVos.stream().collect(Collectors.toMap(WareSkuStockVo::getSkuId,
                item -> {
                    return item.getSkuStockCount() != null && item.getSkuStockCount() > 0;
                }));
    }

    /**
     * 为某个订单锁定库存
     * @param skuLocks 需要锁库存的订单项
     * @return
     */
    @Transactional(rollbackFor = RuntimeException.class)
    @Override
    public Boolean orderLock(WareSkuLockVo skuLocks) {

        /**
         * 锁定库存时先保存工作单主数据的信息
         */
        WareOrderTaskEntity orderTaskEntity = new WareOrderTaskEntity();
        orderTaskEntity.setOrderSn(skuLocks.getOrderSn());
        orderTaskEntity.setCreateTime(new Date());
        orderTaskService.save(orderTaskEntity);

        //1、按照下单的收货地址，找到一个就近仓库，锁定库存
        //2、找到每个商品在哪个仓库都有库存
        List<OrderItemVo> locks = skuLocks.getLocks();
        List<Long> skuIds = locks.stream().map(OrderItemVo::getSkuId).collect(Collectors.toList());
        /**
         * 每个商品对应有库存的仓库id集合
         */
        Map<Long, List<Long>> stockWareMap = listWareSkuHashStock(skuIds);
        if(stockWareMap == null){
            throw new NoStockException("部分商品库存不足");
        }
        //依次为每件商品锁定库存
        //1、如果每一个商品都锁定成功,将当前商品锁定了几件的工作单记录发给MQ
        //2、锁定失败。前面保存的工作单信息都会回滚, 锁定库存的消息到时间释放后，首先去查库存工作单，没有查询到工作单，所以不用解锁库存
        for (OrderItemVo lock : locks) {
            boolean lockStock = false;
            List<Long> wareIds = stockWareMap.get(lock.getSkuId());
            if(CollectionUtils.isEmpty(wareIds)){
                throw new NoStockException(lock.getSkuId());
            }
            for(Long wareId:wareIds){
                long count = this.baseMapper.lockSkuStock(lock.getSkuId(),wareId,lock.getCount());
                if(count == 1){
                    lockStock = true;
                    //锁成功,保存库存工作单详情，同时向mq发送消息
                    //保存工作单详情
                    WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity();
                    taskDetailEntity.setTaskId(orderTaskEntity.getId());
                    taskDetailEntity.setSkuId(lock.getSkuId());
                    taskDetailEntity.setSkuName(lock.getTitle());
                    taskDetailEntity.setSkuNum(lock.getCount());
                    taskDetailEntity.setWareId(wareId);
                    taskDetailEntity.setLockStatus(1);

                    taskDetailService.save(taskDetailEntity);
                    //封装成锁定库存成功的消息
                    StockLockTo stockLockTo = new StockLockTo();
                    stockLockTo.setTaskId(orderTaskEntity.getId());
                    WareTaskDetailVo taskDetailVo = new WareTaskDetailVo();
                    BeanUtils.copyProperties(taskDetailEntity,taskDetailVo);
                    stockLockTo.setTaskDetail(taskDetailVo);

                    //TODO 将锁定详情信息发送给mq(以什么路由发送给哪个交换机)
                    rabbitTemplate.convertAndSend(WareConstant.WARE_EVENT_EXCHANGE,WareConstant.WARE_STOCK_LOCKED_ROUTE,stockLockTo);

                    break;
                }
            }
            //对于当前这个skuId每个仓库都尝试锁库存没有成功，那么也没有锁定库存成功
            if(!lockStock){
                throw new NoStockException(lock.getSkuId());
            }
        }
        //所有商品都遍历完了
        return true;
    }

    /**
     * 库存死信队列的消息释放后解锁锁定的库存（监听锁定库存的消息）
     * @param stockLockTo
     */
    @Override
    public void unlockStock(StockLockTo stockLockTo){
        WareTaskDetailVo taskDetail = stockLockTo.getTaskDetail();
        /**
         * 解锁
         * 1、查询数据库关于这个订单锁定库存信息
         *   有：证明库存锁定成功了
         *      解锁：订单状况
         *          1、没有这个订单，必须解锁库存
         *          2、有这个订单，不一定解锁库存
         *              订单状态：已取消：解锁库存
         *                      已支付：不能解锁库存
         */
        WareOrderTaskDetailEntity taskDetailEntity = taskDetailService.getById(taskDetail.getId());
        //如果为空说明保存库存工作单详情时回滚了
        if(taskDetailEntity == null){
            //无须解锁
            return;
        }
        //有锁定记录 判断订单状态
        Long taskId = stockLockTo.getTaskId();
        WareOrderTaskEntity taskEntity = orderTaskService.getById(taskId);
        //远程调用查询订单信息
        OrderVo orderInfo = null;
        try{
            orderInfo  = orderFeignService.queryInfoByOrderSn(taskEntity.getOrderSn());
        }catch (Exception e){
            //抛异常，重新消费
            throw new RuntimeException("查询的订单异常");
        }
        //如果订单数据回滚了或者订单状态 = 4 订单未支付（关单状态）才需要解锁库存
        if(orderInfo == null || orderInfo.getStatus() == 4){
            //库存工作单详情为已锁定状态，才需要解锁库存
            if(taskDetailEntity.getLockStatus() == 1){
                //消费成功
                unlockStockDetail(taskDetail.getSkuId(),taskDetail.getWareId(),taskDetail.getSkuNum(),taskDetail.getId());
            }
        }
    }

    /**
     * 防止订单服务卡顿，导致订单状态消息一直没有修改，库存消息优先到期之后，查询订单还是新建状态，导致锁定库存没有释放
     * 通过订单类型到的消息解锁库存
     * @param order
     */
    @Transactional
    @Override
    public void unlockStock(OrderVo order) {
        String orderSn = order.getOrderSn();
        //根据订单号查询库存工作单
        WareOrderTaskEntity taskInfo = orderTaskService.getTaskInfoByOrderSn(orderSn);
        //根据taskInfo查询库存锁定工作单详情中的已锁定的记录进行解锁
        List<WareOrderTaskDetailEntity> details = taskDetailService.getTaskDetailByTaskId(taskInfo.getId());
        for (WareOrderTaskDetailEntity detail : details) {
            unlockStockDetail(detail.getSkuId(),detail.getWareId(),detail.getSkuNum(),detail.getId());
        }
    }

    /**
     * 解锁库存的方法(将之前锁定的库存量减掉)
     * @param skuId 商品id
     * @param wareId 仓库id
     * @param skuNum 解锁件数
     * @param id
     */
    private void unlockStockDetail(Long skuId, Long wareId, Integer skuNum, Long id) {
        //退回锁定的库存
        baseMapper.unlockSkuStock(skuId,wareId,skuNum);

        //更新库存锁定详情工作单
        WareOrderTaskDetailEntity taskDetailEntity = new WareOrderTaskDetailEntity();
        taskDetailEntity.setId(id);
        taskDetailEntity.setLockStatus(2);
        taskDetailService.updateById(taskDetailEntity);

    }

    /**
     * 查询每个订单项都在哪些仓库有库存
     * @param skuIds 商品ids
     * @return key skuId value--wareIds
     */
    private Map<Long,List<Long>> listWareSkuHashStock(List<Long> skuIds){
        List<WareSkuEntity> stockWares = this.baseMapper.listWareSkuHashStock(skuIds);
        if(CollectionUtils.isEmpty(stockWares)){
            return null;
        }
        Map<Long,List<Long>> stockMap = new HashMap<>();
        for (Long skuId : skuIds) {
            List<Long> wareIds = stockWares.stream().filter(item -> skuId.equals(item.getSkuId())).map(WareSkuEntity::getWareId)
                    .collect(Collectors.toList());
            stockMap.put(skuId,wareIds);
        }
        return stockMap;
    }

}
