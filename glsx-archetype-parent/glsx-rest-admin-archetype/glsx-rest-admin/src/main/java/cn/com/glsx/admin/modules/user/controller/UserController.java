package cn.com.glsx.admin.modules.user.controller;

import cn.com.glsx.admin.modules.BaseController;
import cn.com.glsx.admin.modules.user.service.UserService;
import cn.com.glsx.admin.services.userservice.model.UserDTO;
import cn.com.glsx.vasp.modules.model.UserSearch;
import cn.com.glsx.echocenter.api.EchoCenterFeignClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.glsx.plat.core.web.R;
import cn.com.glsx.vasp.modules.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 单体应用Controller处理统一返回体
 *
 * @author payu
 */
@Slf4j
@RestController
@RequestMapping(value = "/user")
public class UserController extends BaseController {

    @Resource
    private UserService userService;

    @Resource
    private EchoCenterFeignClient echoCenterFeignClient;

    @GetMapping("/echo")
    public R echo(@RequestParam String message) {
        String echo = echoCenterFeignClient.echo(message);
        return R.ok().data(echo);
    }

    @GetMapping("/search")
    public R search(UserSearch search) {
        PageHelper.startPage(search.getPageNumber(), search.getPageSize());
        PageInfo<UserDTO> list = userService.search(search);
        return R.ok().putPageData(list);
    }

//    @NoLogin
//    @CheckSign
//    @PostMapping(value = "/add")
//    public R add(@RequestBody @Validated UserDTO userDTO) {
//        User user = userDTO.convertTo();
//        User savedUser = userService.addUser(user);
//        UserDTO result = userDTO.convertFor(savedUser);
//        return R.ok().data(result);
//    }
//
//    @PostMapping(value = "/edit")
//    public R edit(@RequestBody @Validated UserDTO userDTO) {
//        User user = userDTO.convertTo();
//        User editUser = userService.editUser(user);
//        UserDTO result = userDTO.convertFor(editUser);
//        return R.ok();
//    }

    @GetMapping(value = "/info/{id}")
    public R info(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        return R.ok().data(user);
    }

    @GetMapping(value = "/info/phone")
    public R info(String phone) {
        User user = userService.findByPhone(phone);
        return R.ok().data(user);
    }

}
