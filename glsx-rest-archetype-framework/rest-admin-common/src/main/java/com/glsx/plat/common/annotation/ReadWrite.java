package com.glsx.plat.common.annotation;

import java.lang.annotation.*;

/**
 * 读写注解
 *
 * @author payu
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReadWrite {
}
