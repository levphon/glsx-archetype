package com.glsx.plat.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 重复提交检查注解
 *
 * @author payu
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface NoResubmit {

    /**
     * 默认时间3秒
     */
    int time() default 3 * 1000;

}
