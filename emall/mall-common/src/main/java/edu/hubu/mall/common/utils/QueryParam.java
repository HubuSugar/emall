package edu.hubu.mall.common.utils;

import lombok.Data;

/**
 * @Description:
 * @Author: huxiaoge
 * @Date: 2021-07-05
 **/
@Data
public class QueryParam {

    private Long key;

    private Long promotionId;

    private Long promotionSessionId;

    private String name;

    private Long pageNo;

    private Long pageSize;
}
