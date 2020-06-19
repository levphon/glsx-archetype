package com.glsx.plat.common.annotation;

import java.lang.annotation.*;

/**
 * 〈一句话功能简述〉<br>
 *
 * @author payu
 * @create 2019/4/8 20:27
 * @since 1.0.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataInitAspect {
}
