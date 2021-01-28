package com.atguigu.gulimail.product.exception;

import com.atguigu.common.exception.BizCodeEnume;
import com.atguigu.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 集中处理异常
 *
 * @author Administrator
 * @Date 2021年1月26日15:09:35
 */
@Slf4j
@RestControllerAdvice(basePackages = "com.atguigu.gulimail.product.controller")
public class GulimailExceptionControllerAdvice {


    /**
     * 数据校验全局处理异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleVaildException(MethodArgumentNotValidException e) {
        BindingResult result = e.getBindingResult();
        Map<String, String> map = new HashMap<>();
        result.getFieldErrors().forEach(each -> {
            //获取错误字段
            String field = each.getField();
            //获取错误字段的message
            String message = each.getDefaultMessage();
            map.put(field, message);
        });
        return R.error(BizCodeEnume.VAILD_Exception.getCode(), BizCodeEnume.VAILD_Exception.getMessage()).put("data", map);
    }


    @ExceptionHandler(value = Throwable.class)
    public R handException(Throwable throwable) {
        log.error(throwable.getMessage());
        return R.error(BizCodeEnume.UNKNOW_Exception.getCode(),BizCodeEnume.UNKNOW_Exception.getMessage());
    }

}
