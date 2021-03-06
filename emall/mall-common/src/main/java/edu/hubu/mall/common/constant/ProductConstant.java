package edu.hubu.mall.common.constant;

/**
 * @Author: huxiaoge
 * @Date: 2021/4/27
 * @Description: 商品服务常量
 **/
public class ProductConstant {

    /**
     * 商品服务顶级分类id
     */
    public static final long PRODUCT_TOP_CATALOGID = 0;

    /**
     * 商品服务redis服务对应的键
     */
    public static final String PRODUCT_CATALOG_KEY = "product_catalog_key";

    /**
     * 商品分类数据分布式锁对应的键
     */
    public static final String PRODUCT_CATALOG_LOCK_KEY = "product_catagory_lock_key";

    public enum AttrEnum {
        ATTR_TYPE_BASE(1,"基本属性"),
        ATTR_TYPE_SALE(0,"销售属性");

        private int code;

        private String msg;

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        AttrEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

    }


    public enum ProductStatusEnum {
        NEW_SPU(0,"新建"),
        SPU_UP(1,"商品上架"),
        SPU_DOWN(2,"商品下架"),
        ;

        private int code;

        private String msg;

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        ProductStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

    }

}
