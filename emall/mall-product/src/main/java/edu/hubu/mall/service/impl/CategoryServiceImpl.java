package edu.hubu.mall.service.impl;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import edu.hubu.mall.common.constant.Constant;
import edu.hubu.mall.common.constant.ProductConstant;
import edu.hubu.mall.dao.CategoryDao;
import edu.hubu.mall.entity.CategoryEntity;
import edu.hubu.mall.service.CategoryService;
import edu.hubu.mall.vo.Catalog2Vo;
import org.redisson.api.RLock;
import org.redisson.api.RReadWriteLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/21
 * @Description: 商品分类服务
 **/
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redisson;

    @Override
    public List<CategoryEntity> listWithTree() {
        //查出所有的分类数据
        List<CategoryEntity> categories = baseMapper.selectList(null);

        //将查询出的菜单数据包装成树形结构
        List<CategoryEntity> categoryList = categories.stream().filter(category -> category.getParentCid() == 0).peek(menu -> {
            menu.setChildren(getChildrens(menu, categories));
        }).sorted((menu1, menu2) -> {
            return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
        }).collect(Collectors.toList());

        return categoryList;
    }

    /**
     * 根据分类Id批量查询分类数据
     * @param cataLogIds
     * @return
     */
    @Override
    public List<CategoryEntity> queryCategoriesByIds(List<Long> cataLogIds) {
        LambdaQueryWrapper<CategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(CategoryEntity::getCatId,cataLogIds);
        return this.list(queryWrapper);
    }

    /**
     * 查询所有的顶级分类
     * @Cacheable 表示返回的数据是可以缓存的，如果缓存中有，那么不会再调用方法
     * @return
     */
    @Cacheable(value={"catalog"},key="#root.methodName")
    @Override
    public List<CategoryEntity> queryTopCategories() {
        System.out.println("queryTopCategories....");
        LambdaQueryWrapper<CategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CategoryEntity::getParentCid, ProductConstant.PRODUCT_TOP_CATALOGID);
        return list(queryWrapper);
    }

    /**
     *  查询前端需要的分类json数据
     *  1.使用HashMap进行本地缓存,但是在分布式环境下（比如商品服务在多台机器上部署时）
     *  基于本地缓存在分布式问题下到的诸多问题，所以不采用
     *  2，引入基于redis的缓存
     *  @return
     * 1.空结果缓存，解决缓存穿透
     * 2.设置过期时间（加随机值）：解决缓存雪崩
     * 3。加锁：解决缓存击穿
     */
    @Override
    public Map<String, List<Catalog2Vo>> queryCatalogJson() {
        //加入缓存逻辑，请求过来先查询缓存，没有查到数据然后再去查询数据库(还要序列化与反序列化)
        String catalogJson = redisTemplate.opsForValue().get(ProductConstant.PRODUCT_CATALOG_KEY);
        if(StringUtils.isEmpty(catalogJson)){
            //如果缓存中的数据为空,查询数据库并缓存
            System.out.println("缓存未命中......");
            return getCatalogJsonWithRedissonLock();
        }
        System.out.println("缓存命中，直接返回......");
        return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {});
    }

    /**
     * 使用redisson作为分布式锁
     * 缓存数据的一致性
     * 1.双写（改完数据库，更新缓存），2.失效(改完数据库，使缓存失效)
     * 使用读写锁，保证数据一致性
     */
    public Map<String,List<Catalog2Vo>> getCatalogJsonWithRedissonLock(){
        RReadWriteLock rwLock = redisson.getReadWriteLock(ProductConstant.PRODUCT_CATALOG_LOCK_KEY);
        RLock rLock = rwLock.writeLock();
        Map<String, List<Catalog2Vo>> catalogMap;
        try{
            rLock.lock();
            //查数据的时候缓存了redis
            catalogMap = getCatalogJsonFromDb();
        }finally {
            rLock.unlock();
        }
        return catalogMap;
    }

    /**
     * 使用redis的分布式锁
     * @return
     */
    public Map<String, List<Catalog2Vo>> getCatalogJsonWithRedisLock() {
        String uuid = IdUtil.simpleUUID();
        //set NX命令
        Boolean lock = redisTemplate.opsForValue().setIfAbsent(ProductConstant.PRODUCT_CATALOG_LOCK_KEY, uuid,300,TimeUnit.SECONDS);
        if(lock){
            //加锁成功设置过期时间,如果执行到此处程序闪断,那么抢占的锁不会释放，所以加锁和设置过期时间必须同步
            //redisTemplate.expire(ProductConstant.PRODUCT_CATALOG_LOCK_KEY,300,TimeUnit.SECONDS);
            System.out.println("获取分布式锁成功...");
            Map<String, List<Catalog2Vo>> catalogJsonMap;
            try{
                catalogJsonMap = getCatalogJsonFromDb();
            }finally {
                //删锁时也需要是原子操作，lua脚本删锁
                String redisScript = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";
                redisTemplate.execute(new DefaultRedisScript<>(redisScript,Long.class), Arrays.asList(ProductConstant.PRODUCT_CATALOG_LOCK_KEY),uuid);
            }
            //删锁的已经过期，可能会删掉其他线程的锁，抢占锁的时候，设置值为当前线程对应的uuid的值
            //redisTemplate.delete(ProductConstant.PRODUCT_CATALOG_LOCK_KEY);   //删除分布式锁
//            String redisUuid = redisTemplate.opsForValue().get(ProductConstant.PRODUCT_CATALOG_LOCK_KEY);
//            //确保是当前线程的锁才删除
//            if(uuid.equals(redisUuid)){
//                redisTemplate.delete(ProductConstant.PRODUCT_CATALOG_LOCK_KEY);
//            }
            return catalogJsonMap;
        }else{
            //加锁失败...通过自旋方式重试去抢占锁，可以休眠100ms
            System.out.println("获取分布式锁失败...等待重试");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getCatalogJsonWithRedisLock();
        }
    }

    /**
     * <b>使用本地锁的方式</b>
     * 锁住当前对象的实例，因为在spring容器中，所有对象都是单例的
     * 封装从数据查询分类数据,进入查数据库的方法，开始竞争锁
     * 两种加锁方式，第一种，对当前实例对象加锁，第二种，直接对当前方法加锁
     * @return
     */
    //TODO 是本地锁，只能锁住当前进程，synchronized,JUC（Lock）
    public Map<String, List<Catalog2Vo>> getCatalogJsonWithLocalLock() {
        synchronized (this) {
            return getCatalogJsonFromDb();
        }
    }

    /**
     * 从数据查询分类数据方法
     * @return
     */
    private Map<String, List<Catalog2Vo>> getCatalogJsonFromDb(){
        //加锁成功，开始执行业务
        String catagoryStr = redisTemplate.opsForValue().get(ProductConstant.PRODUCT_CATALOG_KEY);
        if (!StringUtils.isEmpty(catagoryStr)) {
            return JSON.parseObject(catagoryStr, new TypeReference<Map<String, List<Catalog2Vo>>>() {
            });
        }
        System.out.println("查询数据库......");
        //1.只查询一次数据库，一次将所有分类数据查询过来
        List<CategoryEntity> categories = baseMapper.selectList(null);
        //2.1 筛选出所有的一级分类节点
        List<CategoryEntity> topCategories = getChildrens(categories, ProductConstant.PRODUCT_TOP_CATALOGID);
        //2.2 封装数据
        Map<String, List<Catalog2Vo>> catalogJsonMap = topCategories.stream().collect(Collectors.toMap(k -> String.valueOf(k.getCatId()), v -> {
            //2.3 查询二级菜单数据
            return getChildrens(categories, v.getCatId()).stream().map(l2 -> {
                //2.4 查询三级菜单列表
                List<Catalog2Vo.CatalogLeaf> catalogLeafList = getChildrens(categories, l2.getCatId()).stream().map(l3 -> {
                    return new Catalog2Vo.CatalogLeaf(String.valueOf(l2.getCatId()), String.valueOf(l3.getCatId()), l3.getName());
                }).collect(Collectors.toList());

                return new Catalog2Vo(String.valueOf(v.getCatId()), catalogLeafList, String.valueOf(l2.getCatId()), l2.getName());
            }).collect(Collectors.toList());
        }));

        //在同一把锁内完成数据库的查询和写缓存
        String catalogJson = JSON.toJSONString(catalogJsonMap);
        redisTemplate.opsForValue().set(ProductConstant.PRODUCT_CATALOG_KEY, catalogJson, 30, TimeUnit.MINUTES);
        return catalogJsonMap;
    }

    private List<CategoryEntity> getChildrens(List<CategoryEntity> allCategories,Long pid){
        return  allCategories.stream().filter(item -> item.getParentCid().equals(pid)).collect(Collectors.toList());
    }

    /**
     * 根据一个分类查找子分类
     * @param root 当前菜单节点
     * @param categories 所有分类数据
     * @return 子节点结果集
     */
    private List<CategoryEntity> getChildrens(CategoryEntity root,List<CategoryEntity> categories){
        List<CategoryEntity> childrens = categories.stream()
                .filter(category -> category.getParentCid().equals(root.getCatId()))
                .peek(menu -> menu.setChildren(getChildrens(menu, categories)))
                .sorted((menu1, menu2) -> {
                    return (menu1.getSort() == null ? 0 : menu1.getSort()) - (menu2.getSort() == null ? 0 : menu2.getSort());
                }).collect(Collectors.toList());
        return childrens;
    }
}
