package com.atguigu.gulimail.member.exception;

/**
 * 用户名已经存在异常
 */
public class UserNameExistException extends RuntimeException {
    public UserNameExistException() {
        super("用户名已经存在");
    }
}
