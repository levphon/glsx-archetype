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
     * 需要进行过滤的连表
     *
     * @return
     */
    String linkTable() default "t_user";

    /**
     * 需要进行过滤的连表id(增删改操作不需要)
     *
     * @return
     */
    String linkField() default "id";

    /**
     * 是否显示增强 sql 内容
     *
     * @return
     */
    boolean showSql() default false;

}
