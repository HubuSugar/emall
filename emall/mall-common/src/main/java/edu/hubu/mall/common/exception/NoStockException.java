package edu.hubu.mall.common.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author: huxiaoge
 * @Date: 2021/6/5
 * @Description:
 **/
public class NoStockException extends RuntimeException {

    @Getter @Setter
    private Long skuId;

    public NoStockException(Long skuId){
        super(skuId + "号商品没有足够的库存了");
    }

    public NoStockException(String msg) {
        super(msg);
    }

}
