package edu.hubu.mall.common.utils;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import edu.hubu.mall.common.filter.SQLFilter;
import edu.hubu.mall.common.constant.Constant;
import org.apache.commons.lang.StringUtils;

import java.util.Map;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/26
 * @Description: 分页查询包装对象
 **/
public class Query<T> {

    public IPage<T> getPage(Map<String,Object> paramMap){
        return getPage(paramMap,null,false);
    }

    public IPage<T> getPage(Map<String,Object> paramMap,String defaultOrderField,boolean isAsc){
        //当前页
        long pageNo = Constant.DEFAULT_PAGE_NO;
        long pageSize = Constant.DEFAULT_PAGE_SIZE;

        if(paramMap.containsKey(Constant.PAGE_NO)){
            pageNo = Long.parseLong((String)paramMap.get(Constant.PAGE_NO));
        }
        if(paramMap.containsKey(Constant.PAGE_SIZE)){
            pageSize = Long.parseLong((String)paramMap.get(Constant.PAGE_SIZE));
        }

        //分页对象
        Page<T> page = new Page<>(pageNo, pageSize);

        //分页参数
        paramMap.put(Constant.PAGE_NO, page);

        //排序字段
        //防止SQL注入（因为sidx、order是通过拼接SQL实现排序的，会有SQL注入风险）
        String orderField = SQLFilter.sqlInject((String)paramMap.get(Constant.ORDER_FIELD));
        String order = (String)paramMap.get(Constant.ORDER);


        //前端字段排序
        if(StringUtils.isNotEmpty(orderField) && StringUtils.isNotEmpty(order)){
            if(Constant.ASC.equalsIgnoreCase(order)) {
                return  page.addOrder(OrderItem.asc(orderField));
            }else {
                return page.addOrder(OrderItem.desc(orderField));
            }
        }

        //没有排序字段，则不排序
        if(StringUtils.isBlank(defaultOrderField)){
            return page;
        }

        //默认排序
        if(isAsc) {
            page.addOrder(OrderItem.asc(defaultOrderField));
        }else {
            page.addOrder(OrderItem.desc(defaultOrderField));
        }

        return page;
    }

}
