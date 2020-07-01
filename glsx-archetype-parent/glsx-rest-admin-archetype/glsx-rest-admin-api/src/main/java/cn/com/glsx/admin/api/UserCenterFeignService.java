package cn.com.glsx.admin.api;

import cn.com.glsx.admin.fallback.UserCenterFeignFactory;
import cn.com.glsx.admin.services.userservice.dto.UserDTO;
import cn.com.glsx.admin.services.userservice.dto.UserSearch;
import com.github.pagehelper.PageInfo;
import com.glsx.vasp.modules.entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author payu
 */
@FeignClient(name = "glsx-rest-admin", fallbackFactory = UserCenterFeignFactory.class, decode404 = true)
public interface UserCenterFeignService {

    @GetMapping("/rest/user/echo")
    String echo(@RequestParam String message);

    @GetMapping("/rest/user/search")
    PageInfo<User> search(UserSearch search);

    @PostMapping(value = "/rest/user/add")
    UserDTO add(@RequestBody @Validated UserDTO userDTO);

    @PostMapping(value = "/rest/user/edit")
    UserDTO edit(@RequestBody @Validated UserDTO userDTO);

    @GetMapping(value = "/rest/user/info/{id}")
    User info(@PathVariable("id") Long id);

    @GetMapping(value = "/rest/user/info/phone")
    User info(String phone);

}
