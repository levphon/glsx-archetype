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
     * 用户标识
     */
    private Long userId;

    /**
     * 用户账号（如：管理系统admin，消费者系统手机号等）
     */
    private String account;

    /**
     * 归属（租户、组织、机构、部门、个体等等）
     */
    private String belong;

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
