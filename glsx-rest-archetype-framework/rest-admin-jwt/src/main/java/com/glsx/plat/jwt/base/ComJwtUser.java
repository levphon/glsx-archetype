package com.glsx.plat.jwt.base;

import lombok.Data;

/**
 * 通用jwt与数据库用户实体映射关联
 *
 * @author payu
 */
@Data
public class ComJwtUser extends BaseJwtUser {

    @Override
    public String getClazz() {
        return this.getClass().getCanonicalName();
    }

}
