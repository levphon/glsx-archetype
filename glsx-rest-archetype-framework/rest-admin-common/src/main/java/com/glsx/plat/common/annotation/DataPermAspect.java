package com.glsx.plat.common.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataPermAspect {
    /**
     * 是否显示增强 sql 内容
     * @return
     */
    boolean showSql() default false;

    /**
     * 表别名前缀; 如果只有一张表可以写空串
     * @return
     */
    String tablePerix();
}
