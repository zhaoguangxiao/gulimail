package com.atguigu.common.constant;

/**
 * 产品常量类
 */
public class ProductConstant {


    public enum AttrConstantEnum {

        ATTR_TYPE_BASE(1, "基本属性"),
        ATTR_TYPE_SALE(0, "销售属性");
        private int code;
        private String msg;

        AttrConstantEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }
    }

    public enum StatusEnum{

        CREATE_ENUM(0,"新建"),
        UP_ENUM(1,"上架"),
        LOWER_ENUM(2,"下架");

        private int code;
        private String message;

        StatusEnum(int code, String message) {
            this.code = code;
            this.message = message;
        }

        public int getCode() {
            return code;
        }

        public String getMessage() {
            return message;
        }
    }



}
