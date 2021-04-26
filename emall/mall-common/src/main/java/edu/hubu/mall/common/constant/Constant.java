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
