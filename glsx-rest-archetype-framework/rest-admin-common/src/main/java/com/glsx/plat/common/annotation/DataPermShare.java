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
public @interface DataPermShare {

    /**
     * 需要进行过滤的连表后缀，完整表面为 业务表名_suffix
     *
     * @return
     */
    String permSuffix() default "_permit";

    /**
     * 连接数据权限的表，不设置默认会取主表
     *
     * @return
     */
    String linkTable() default "";

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

    /**
     * 查询数据读写权限类型的列名
     *
     * @return
     */
    String permitTypeField() default "";

}
