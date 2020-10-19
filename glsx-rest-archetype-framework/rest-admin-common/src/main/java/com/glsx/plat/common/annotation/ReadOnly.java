package com.glsx.plat.common.annotation;

import java.lang.annotation.*;

/**
 * 只读注解
 *
 * @author payu
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ReadOnly {
}
