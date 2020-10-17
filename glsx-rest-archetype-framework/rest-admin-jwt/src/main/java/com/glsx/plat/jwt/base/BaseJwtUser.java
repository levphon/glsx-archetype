package com.glsx.plat.jwt.base;

import lombok.Getter;
import lombok.Setter;

/**
 * @author payu
 */
@Setter
@Getter
public abstract class BaseJwtUser {

    /**
     * jwt标识
     */
    private String jwtId;

    /**
     * 用户标识
     */
    private Long userId;

    /**
     * 用户账号（如：管理系统admin，消费者系统手机号等）
     */
    private String account;

    /**
     * 租户
     */
    private String tenant;

    /**
     * 归属（组织、机构、部门、个体等等）
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
