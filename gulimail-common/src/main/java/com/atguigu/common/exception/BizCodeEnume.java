package com.atguigu.common.exception;

/**
 * 错误码和错误信息定义类
 * 1),错误码定义规范为5位数字
 * 2), 前2位表示业务场景,最后三位表示错误码,例如 10001 10通用 001 系统未知异常
 * 3), 维护错误码后需要维护错误描述,将它们定义为枚举形式
 * 10 通用
 * 001 参数校验格式异常
 * 11:商品
 * 12:订单
 * 13:购物车
 * 14:物流
 *
 * @author Administrator
 * @Date 2021年1月26日15:54:33
 */

public enum BizCodeEnume {

    UNKNOW_Exception(1000, "系统未知异常"),
    VAILD_Exception(10001, "参数格式校验失败"),
    DELETION_FAILED(10002, "无法进行删除,含有子分类"),
    PRODUCT_UP_FAILED(11000,"商品上架异常");

    private int code;
    private String message;

    BizCodeEnume(int code, String message) {
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
