package cn.com.glsx.admin.fallback;

import cn.com.glsx.admin.api.UserCenterFeignClient;
import cn.com.glsx.admin.services.userservice.model.User;
import cn.com.glsx.admin.services.userservice.model.UserDTO;
import cn.com.glsx.admin.services.userservice.model.UserSearch;
import com.github.pagehelper.PageInfo;
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
    public User add(UserDTO userDTO) {
        return null;
    }

    @Override
    public User edit(UserDTO userDTO) {
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