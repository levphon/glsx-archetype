package cn.com.glsx.admin.modules.user.controller;

import cn.com.glsx.admin.modules.BaseController;
import cn.com.glsx.admin.modules.user.dto.UserDTO;
import cn.com.glsx.admin.modules.user.dto.UserSearch;
import cn.com.glsx.admin.modules.user.service.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.glsx.plat.core.web.R;
import com.glsx.vasp.modules.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author payu
 */
@Slf4j
@RestController
@RequestMapping(value = "/user")
public class UserController extends BaseController {

    @Resource
    private UserService userService;

//    @Resource
//    private EchoCenterFeignService echoCenterFeignService;

//    @GetMapping("/echo")
//    public R echo(@RequestParam String message) {
//        String echo = echoCenterFeignService.echo(message);
//        return R.ok().data(echo);
//    }
//
//    @GetMapping("/echo2")
//    public R echo2(@RequestParam String title, @RequestParam String message) {
//        String echo = echoCenterFeignService.echo2(title, message);
//        return R.ok().data(echo);
//    }
//
//    @SysLog
//    @NoLogin
//    @CheckSign
//    @PostMapping("/echo3")
//    public R echo3(@RequestBody EchoReq echoReq) {
//        EchoResp echo = echoCenterFeignService.echo3(echoReq);
//        return R.ok().data(echo);
//    }

    @GetMapping("/search")
    public R search(UserSearch search) {
        PageHelper.startPage(search.getPageNumber(), search.getPageSize());
        PageInfo<User> list = userService.search(search);
        return R.ok().putPageData(list);
    }

    @PostMapping(value = "/add")
    public R add(@Validated UserDTO userDTO, BindingResult bindingResult) throws BindException {
        checkDTOParams(bindingResult);

        User user = userDTO.convertTo();
        User savedUser = userService.addUser(user);
        UserDTO result = userDTO.convertFor(savedUser);
        return R.ok().data(result);
    }

    @PostMapping(value = "/edit")
    public R edit(@Validated UserDTO userDTO, BindingResult bindingResult) throws BindException {
        checkDTOParams(bindingResult);

        User user = userDTO.convertTo();
        User editUser = userService.editUser(user);
        UserDTO result = userDTO.convertFor(editUser);
        return R.ok();
    }

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

//    @PostMapping("/upload")
//    public R upload(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
//        String path = fastdfsUtils.uploadFile(file);
//        log.info("上传文件路径==" + path);
//        return R.ok().data(path);
//    }

}
