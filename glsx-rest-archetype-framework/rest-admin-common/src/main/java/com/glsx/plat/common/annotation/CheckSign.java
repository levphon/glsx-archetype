package com.glsx.plat.common.annotation;

import java.lang.annotation.*;

/**
 * @author payu
 */
@NoLogin
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CheckSign {

}