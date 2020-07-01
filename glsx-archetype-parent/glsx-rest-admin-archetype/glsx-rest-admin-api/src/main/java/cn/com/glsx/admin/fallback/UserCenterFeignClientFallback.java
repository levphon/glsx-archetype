package cn.com.glsx.admin.fallback;

import cn.com.glsx.admin.api.UserCenterFeignClient;
import cn.com.glsx.admin.services.userservice.dto.UserDTO;
import cn.com.glsx.admin.services.userservice.dto.UserSearch;
import com.github.pagehelper.PageInfo;
import com.glsx.vasp.modules.entity.User;
import org.springframework.stereotype.Component;

/**
 * Feign Fallback 处理
 *
 * @author payu
 */
@Component
public class UserCenterFeignClientFallback implements UserCenterFeignClient {

    @Override
    public String echo(String message) {
        return null;
    }

    @Override
    public PageInfo<User> search(UserSearch search) {
        return null;
    }

    @Override
    public UserDTO add(UserDTO userDTO) {
        return null;
    }

    @Override
    public UserDTO edit(UserDTO userDTO) {
        return null;
    }

    @Override
    public User info(Long id) {
        return null;
    }

    @Override
    public User info(String phone) {
        return null;
    }

}