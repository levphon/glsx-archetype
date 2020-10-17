package cn.com.glsx.admin.services.userservice.model;

import lombok.Data;

/**
 * @author payu
 */
@Data
public class UserDTO {

    private Long id;

    private String username;

    private String realname;

    private String password;

    private String phone;

    private String remark;

    private Long[] roleIds;

}
