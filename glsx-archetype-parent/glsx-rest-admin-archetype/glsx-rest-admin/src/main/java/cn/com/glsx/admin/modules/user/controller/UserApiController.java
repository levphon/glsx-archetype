package cn.com.glsx.admin.modules.user.controller;

import cn.com.glsx.admin.modules.BaseController;
import cn.com.glsx.admin.modules.user.service.UserService;
import cn.com.glsx.admin.services.userservice.model.UserSearch;
import cn.com.glsx.echocenter.api.EchoCenterFeignClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.glsx.vasp.modules.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 微服务应用在网关中处理统一返回体
 *
 * @author payu
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/user")
public class UserApiController extends BaseController {

    @Resource
    private UserService userService;

    @Resource
    private EchoCenterFeignClient echoCenterFeignClient;

    @GetMapping("/echo")
    public String echo(@RequestParam String message) {
        String echo = echoCenterFeignClient.echo(message);
        return echo;
    }

    @GetMapping("/search")
    public PageInfo<User> search(UserSearch search) {
        PageHelper.startPage(search.getPageNumber(), search.getPageSize());
        PageInfo<User> list = userService.search(search);
        return list;
    }

//    @PostMapping(value = "/add")
//    public UserDTO add(@RequestBody @Validated UserDTO userDTO) {
//        User user = userDTO.convertTo();
//        User savedUser = userService.addUser(user);
//        UserDTO result = userDTO.convertFor(savedUser);
//        return result;
//    }
//
//    @PostMapping(value = "/edit")
//    public UserDTO edit(@RequestBody @Validated UserDTO userDTO) {
//        User user = userDTO.convertTo();
//        User editUser = userService.editUser(user);
//        UserDTO result = userDTO.convertFor(editUser);
//        return result;
//    }

//    @GetMapping(value = "/info/{id}")
//    public UserDTO info(@PathVariable("id") Long id) {
//        User user = userService.getById(id);
//        UserDTO result = new UserDTO().convertFor(user);
//        return result;
//    }
//
//    @GetMapping(value = "/info/phone")
//    public UserDTO info(String phone) {
//        User user = userService.findByPhone(phone);
//        UserDTO result = new UserDTO().convertFor(user);
//        return result;
//    }

}
