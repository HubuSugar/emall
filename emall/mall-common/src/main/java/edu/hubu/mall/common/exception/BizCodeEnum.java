package edu.hubu.mall.common.exception;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/26
 * @Description: 业务异常的枚举类
 **/
public enum BizCodeEnum {

    PRODUCT_UP_EXCEPTION(21001,"商品上架失败");

    private Integer code;
    private String msg;

    BizCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
