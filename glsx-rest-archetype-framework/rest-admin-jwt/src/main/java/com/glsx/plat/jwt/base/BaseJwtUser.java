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
    private String userId;

    /**
     * 用户账号（如：管理系统admin，消费者系统手机号,openid等）
     */
    private String account;

    /**
     * 角色id，多个英文逗号分割
     */
    private String roleIds;

    /**
     * 终端系统：如：管理系后台，小程序后台，H5后台等
     */
    private String terminal;

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
