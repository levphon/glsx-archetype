package com.glsx.plat.common.enums;

/**
 * 用于注解  @SysLogMark
 * 保存参数方式
 *
 * @author payu
 */
public enum RequestSaveMethod {
    // 从Request 对象中拿,用request.getParamMap
    REQUEST,
    // 使用反射从方法参数中拿
    REFLECT
}
