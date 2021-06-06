package cn.com.glsx.admin.modules.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.glsx.plat.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Accessors(chain = true)
@Data
@Entity
@Table(name = "d_user")
public class User extends BaseEntity {

    /**
     * 帐号
     */
    @Column(name = "account", length = 64)
    private String account;

    /**
     * 密码
     */
    @JsonIgnore
    @Column(name = "password", length = 128)
    private String password;

    /**
     * 用户名
     */
    @Column(name = "username", length = 64)
    private String username;

    /**
     * （真实）姓名
     */
    @Column(name = "realname", length = 64)
    private String realname;

    /**
     * 手机号
     */
    @Column(name = "phone", length = 20)
    private String phone;

    /**
     * 头像链接
     */
    @Column(name = "avatar", length = 150)
    private String avatar;

    /**
     * 加密盐
     */
    private String salt;

}
