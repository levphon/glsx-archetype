package com.glsx.plat.jwt.base;

import lombok.Data;

/**
 * @author payu
 */
@Data
public abstract class BaseJwtUser {

    /**
     * jwt标识
     */
    private String jwtId;

    /**
     * 应用/服务名称
     */
    private String application;

    /**
     * 用户标识（用long，）
     */
    private String userId;

    /**
     * 用户账号（如：管理系统admin，消费者系统手机号等）
     */
    private String account;

    /**
     * 租户
     */
    private String tenant;

    /**
     * 子类的class全限定名
     */
    private String clazz;

    /**
     * 子类的class全限定名
     *
     * @return
     */
    public abstract String getClazz();

}
