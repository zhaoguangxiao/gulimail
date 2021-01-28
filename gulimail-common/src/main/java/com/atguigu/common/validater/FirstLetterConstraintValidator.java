package com.atguigu.common.validater;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 自定义注解实现类
 *
 * @author Administrator
 */
@Slf4j
public class FirstLetterConstraintValidator implements ConstraintValidator<FirstLetter, String> {

    private static final Pattern pattern = Pattern.compile("[a-zA-Z]");

    /**
     * 初始化方法
     *
     * @param constraintAnnotation
     */
    @Override
    public void initialize(FirstLetter constraintAnnotation) {

    }

    /**
     * 判断是否校验成功逻辑
     *
     * @param s                          需要校验的值
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (StringUtils.isEmpty(s)) return true;
        Matcher m = pattern.matcher(s);
        log.info("传入值为 {},结果为{}",s,m.matches());
        return m.matches();
    }
}
