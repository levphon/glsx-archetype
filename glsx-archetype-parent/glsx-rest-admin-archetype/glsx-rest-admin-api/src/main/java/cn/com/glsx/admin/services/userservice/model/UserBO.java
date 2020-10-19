package cn.com.glsx.admin.services.userservice.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author payu
 */
@Data
public class UserBO {

    private Long id;

    @NotBlank
    private String username;

    private String realName;

    @NotBlank
    private String password;

    @NotBlank
    private String phone;

    private String remark;

    private Long[] roleIds;

}
