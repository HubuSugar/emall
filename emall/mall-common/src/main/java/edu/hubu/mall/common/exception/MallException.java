package edu.hubu.mall.common.exception;

import lombok.Data;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/26
 * @Description:
 **/
@Data
public class MallException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private String msg;
    private int code = 500;

    public MallException(String msg) {
        super(msg);
        this.msg = msg;
    }

    public MallException(String msg, Throwable e) {
        super(msg, e);
        this.msg = msg;
    }

    public MallException(String msg, int code) {
        super(msg);
        this.msg = msg;
        this.code = code;
    }

    public MallException(String msg, int code, Throwable e) {
        super(msg, e);
        this.msg = msg;
        this.code = code;
    }
}
