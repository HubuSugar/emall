package edu.hubu.mall.common.config.filter;

import edu.hubu.mall.common.exception.MallException;
import org.apache.commons.lang.StringUtils;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/26
 * @Description:
 **/
public class SQLFilter {

    /**
     * SQL注入过滤
     * @param str  待验证的字符串
     */
    public static String sqlInject(String str){
        if(StringUtils.isBlank(str)){
            return null;
        }
        //去掉'|"|;|\字符
        str = StringUtils.replace(str, "'", "");
        str = StringUtils.replace(str, "\"", "");
        str = StringUtils.replace(str, ";", "");
        str = StringUtils.replace(str, "\\", "");

        //转换成小写
        str = str.toLowerCase();

        //非法字符
        String[] keywords = {"master", "truncate", "insert", "select", "delete", "update", "declare", "alter", "drop"};

        //判断是否包含非法字符
        for(String keyword : keywords){
            if(str.indexOf(keyword) != -1){
                throw new MallException("SQL语句包含非法字符");
            }
        }

        return str;
    }
}
