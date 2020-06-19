package cn.com.glsx.admin.modules.user.dto;

import com.google.common.base.Converter;
import com.glsx.vasp.modules.entity.User;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotBlank;

/**
 * @author payu
 */
@Data
public class UserDTO {

    @NotBlank
    private String username;

    private String realname;

    @NotBlank
    private String password;

    @NotBlank
    private String phone;

    private String remark;

    private Long[] roleIds;

    public User convertTo() {
        DTOConverter convert = new DTOConverter();
        return convert.convert(this);
    }

    public UserDTO convertFor(User user) {
        DTOConverter convert = new DTOConverter();
        return convert.reverse().convert(user);
    }

    private static class DTOConverter extends Converter<UserDTO, User> {
        @Override
        protected User doForward(UserDTO source) {
            User target = new User();
            BeanUtils.copyProperties(source, target);
            return target;
        }

        @Override
        protected UserDTO doBackward(User source) {
            UserDTO target = new UserDTO();
            BeanUtils.copyProperties(source, target);
            return target;
        }
    }

}
