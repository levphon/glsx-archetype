package com.glsx.plat.context;

import com.glsx.plat.context.configuration.RestAdminAutoConfiguration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * @author payu
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(RestAdminAutoConfiguration.class)
@Documented
@Inherited
public @interface EnableRestAdmin {
}
