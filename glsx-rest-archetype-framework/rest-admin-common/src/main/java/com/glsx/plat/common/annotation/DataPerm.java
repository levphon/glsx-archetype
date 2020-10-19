package com.glsx.plat.common.annotation;

import java.lang.annotation.*;

/**
 * 数据权限注解
 *
 * @author payu
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface DataPerm {

    /**
     * 是否显示增强 sql 内容
     *
     * @return
     */
    boolean showSql() default false;

    /**
     * 表别名; 如果只有一张表可以写空串
     *
     * @return
     */
    String tableAlias() default "";

    /**
     * 追加sql方法名
     *
     * @return
     */
    public String method() default "whereSql";

}
