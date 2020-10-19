package cn.com.glsx;

import cn.com.glsx.admin.modules.user.converter.UserConverter;
import cn.com.glsx.admin.services.userservice.model.UserDTO;
import com.glsx.vasp.modules.entity.User;

public class Test {

    public static void main(String[] args) {
        User user = new User();

        user.setId(1L);

        user.setUsername("Hollis");

        user.setRealname("张三");

        UserDTO userDTO = UserConverter.INSTANCE.do2dto(user);

        System.out.println(userDTO.toString());
    }

}
