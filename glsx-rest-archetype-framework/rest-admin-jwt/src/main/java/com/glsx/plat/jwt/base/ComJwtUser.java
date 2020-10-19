package com.glsx.plat.jwt.base;

import lombok.Data;

/**
 * 通用jwt与数据库用户实体映射关联
 *
 * @author payu
 */
@Data
public class ComJwtUser extends BaseJwtUser {

    /**
     * 用户分组id
     */
    private Long userGroupId;

    /**
     * 租户（公司）id
     */
    private Long tenantId;

    /**
     * 部门id
     */
    private Long deptId;

    /**
     * 角色id
     */
    private Long[] roleIds;

    /**
     * 机构id
     */
    private Long[] orgIds;

    @Override
    public String getClazz() {
        return this.getClass().getCanonicalName();
    }

}
