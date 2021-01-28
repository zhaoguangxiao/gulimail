package com.atguigu.common.validater;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 判断首字母 为a-z里面的一个
 *
 * @author Administrator
 */
@Documented
@Constraint(validatedBy = {FirstLetterConstraintValidator.class})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface FirstLetter {

    String message() default "{com.atguigu.common.validater.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};


}
