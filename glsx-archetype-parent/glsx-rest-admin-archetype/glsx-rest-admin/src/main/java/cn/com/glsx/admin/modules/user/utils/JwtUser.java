package cn.com.glsx.admin.modules.user.utils;

import com.glsx.plat.jwt.base.BaseJwtUser;
import lombok.Getter;
import lombok.Setter;

/**
 * 用于jwt与数据库用户实体映射关联
 *
 * @author payu
 */
@Setter
@Getter
public class JwtUser extends BaseJwtUser {

    private String phone;

    @Override
    public String getClazz() {
        return this.getClass().getCanonicalName();
    }

}