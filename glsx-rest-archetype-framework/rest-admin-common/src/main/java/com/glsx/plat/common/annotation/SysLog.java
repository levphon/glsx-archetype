package com.glsx.plat.common.annotation;

import com.glsx.plat.common.enums.OperateType;
import com.glsx.plat.common.enums.RequestSaveMethod;

import java.lang.annotation.*;

/**
 * 系统日志注解
 *
 * @author payu
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {

    /**
     * 说明描述
     *
     * @return
     */
    String value() default "";

    /**
     * 模块
     *
     * @return
     */
    String module() default "";

    /**
     * 操作
     *
     * @return
     */
    OperateType action() default OperateType.QUERY;

    /**
     * 打印方法执行时间
     *
     * @return
     */
    boolean printSpendTime() default false;

    /**
     * 记录请求数据,对于 base64 等数据需设置为 false
     * 文件流数据不会存储
     *
     * @return
     */
    boolean saveRequest() default true;

    /**
     * 保存参数方法,默认使用 request 中拿
     *
     * @return
     */
    RequestSaveMethod saveRequestMethod() default RequestSaveMethod.REQUEST;

    /**
     * 是否保存日志信息入库
     *
     * @return
     */
    boolean saveLog() default true;

}
