package edu.hubu.mall.service.impl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
     * @return
     */
    @Override
    public List<CategoryEntity> queryTopCategories() {
        LambdaQueryWrapper<CategoryEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(CategoryEntity::getParentCid, ProductConstant.PRODUCT_TOP_CATALOGID);
        return list(queryWrapper);
    }

    /**
     *  查询前端需要的分类json数据
     *  1.使用HashMap进行本地缓存,但是在分布式环境下（比如商品服务在对台机器上部署时）
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
            return getCatalogJsonFromDB();
        }
        System.out.println("缓存命中，直接返回......");
        return JSON.parseObject(catalogJson, new TypeReference<Map<String, List<Catalog2Vo>>>() {});
    }

    /**
     * 封装从数据查询分类数据,进入查数据库的方法，开始竞争锁
     * 两种加锁方式，第一种，对当前实例对象加锁，第二种，直接对当前方法加锁
     * @return
     */
    /**
     * 锁住当前对象的实例，因为在spring容器中，所有对象都是单例的
     */
    //TODO 是本地锁，只能锁住当前进程，synchronized,JUC（Lock）
    public Map<String, List<Catalog2Vo>> getCatalogJsonFromDB() {
        synchronized (this) {
            String s = redisTemplate.opsForValue().get(ProductConstant.PRODUCT_CATALOG_KEY);
            if (!StringUtils.isEmpty(s)) {
                return JSON.parseObject(s, new TypeReference<Map<String, List<Catalog2Vo>>>() {
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
            redisTemplate.opsForValue().set(ProductConstant.PRODUCT_CATALOG_KEY, catalogJson,30, TimeUnit.MINUTES);
            return catalogJsonMap;
        }
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
