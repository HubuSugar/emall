package edu.hubu.mall.common;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/20
 * @Description:
 **/
public enum ResultStatus {

    /**
     * 操作成功的返回状态
     */
    SUCCESS(0,"操作成功");

    private Integer code;
    private String msg;

    ResultStatus(Integer code,String msg) {
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
