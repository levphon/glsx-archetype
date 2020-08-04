package com.glsx.plat.common.annotation;

import java.lang.annotation.*;

/**
 * @author payu
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface RequiredPermission {

    String value();

}
