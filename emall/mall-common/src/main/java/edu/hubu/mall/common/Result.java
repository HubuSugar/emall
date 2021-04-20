package edu.hubu.mall.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/20
 * @Description: 通用返回包装类
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Result<T> implements Serializable {

    /**
     * 是否成功
     */
    private Boolean success;
    /**
     * 状态码
     */
    private Integer code;

    /**
     * 操作成功或者失败的提示信息
     */
    private String msg;

    /**
     * 返回成功到的数据对象
     */
    private T data;
    /**
     *extendMap: 用于分页扩展信息
     */
    private Map<String,Object> extendMap = new HashMap<>();

    public Result(Boolean success, Integer code) {
        this.success = success;
        this.code = code;
    }

    public Result(Boolean success, Integer code, String message) {
        this.success = success;
        this.code = code;
        this.msg = message;
    }

    /**
     * 操作成功的方法
     * @param <T>
     * @return
     */
    public static <T> Result<T> ok(){
        return new Result<>(Boolean.TRUE, ResultStatus.SUCCESS.getCode());
    }

    /**
     * 操作失败到的方法
     * @param code
     * @param msg
     * @return
     */
    public static Result error(Integer code, String msg){
        return new Result<>(Boolean.FALSE, code, msg);
    }



}
