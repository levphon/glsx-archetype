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
     * 拥有数据权限的表，不设置默认会取主表
     *
     * @return
     */
    String permTable() default "";

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
     * 需要直接追加的脚本,如果追加除showSql其它字段不起作用
     *
     * @return
     */
    String appendScript() default "";

    /**
     * 是否显示增强 sql 内容
     *
     * @return
     */
    boolean showSql() default false;

}
