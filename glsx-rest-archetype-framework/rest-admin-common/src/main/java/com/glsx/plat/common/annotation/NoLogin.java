package com.glsx.plat.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author liuyufeng
 * @Title NoLogin.java
 * @Description 登陆校验注解  添加注解，无需登录
 * @date 2019年7月13日 下午2:39:08
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface NoLogin {

    boolean value() default true;

}

