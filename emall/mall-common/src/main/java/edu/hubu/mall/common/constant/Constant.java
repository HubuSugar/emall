package edu.hubu.mall.common.constant;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/23
 * @Description: 常量类
 **/
public class Constant {
    //超级管理员
    public static final long SUPER_ADMIN_ID = 1;

    /**
     * 分页默认页码
     */
    public static final long DEFAULT_PAGE_NO = 1;

    /**
     * 分页默认每页大小数
     */
    public static final long DEFAULT_PAGE_SIZE = 10;

    /**
     * 当前页码
     */
    public static final String PAGE_NO = "pageNo";
    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";
    /**
     * 排序字段
     */
    public static final String ORDER_FIELD = "sidx";
    /**
     * 排序方式
     */
    public static final String ORDER = "order";
    /**
     *  升序
     */
    public static final String ASC = "asc";

    /**
     * 枚举菜单类型
     */
    public enum MenuType {
        /**
         * 目录
         */
        CATALOG(0),
        /**
         * 菜单
         */
        MENU(1),
        /**
         * 按钮
         */
        BUTTON(2);

        private int value;

        MenuType(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }
        public void setValue(int value){
            this.value = value;
        }
    }
}
