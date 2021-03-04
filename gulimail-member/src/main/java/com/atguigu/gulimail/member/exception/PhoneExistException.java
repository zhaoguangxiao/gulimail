package com.atguigu.gulimail.member.exception;

/**
 * 手机号已经存在异常
 */
public class PhoneExistException extends RuntimeException {
    public PhoneExistException() {
        super("手机号已经存在");
    }
}
