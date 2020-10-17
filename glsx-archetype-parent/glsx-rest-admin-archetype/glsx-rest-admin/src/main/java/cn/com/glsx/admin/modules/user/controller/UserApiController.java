package cn.com.glsx.admin.modules.user.controller;

import cn.com.glsx.admin.api.UserCenterFeignClient;
import cn.com.glsx.admin.modules.user.converter.UserConverter;
import cn.com.glsx.admin.modules.user.service.UserService;
import cn.com.glsx.admin.services.userservice.model.UserBO;
import cn.com.glsx.admin.services.userservice.model.UserDTO;
import cn.com.glsx.echocenter.api.EchoCenterFeignClient;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.glsx.plat.context.utils.validator.AssertUtils;
import com.glsx.vasp.modules.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.Map;

/**
 * 微服务应用在网关中处理统一返回体
 *
 * @author payu
 */
@Slf4j
@RestController
@RequestMapping(value = "/api/user")
public class UserApiController implements UserCenterFeignClient {

    @Resource
    private UserService userService;

    @Resource
    private EchoCenterFeignClient echoCenterFeignClient;

    @Override
    @GetMapping("/echo")
    public String echo(@RequestParam String message) {
        String echo = echoCenterFeignClient.echo(message);
        return echo;
    }

    @Override
    @GetMapping("/search")
    public PageInfo<UserDTO> search(@RequestParam Map<String, Object> params) {
        Integer pageNumber = (Integer) params.get("pageNumber");
        Integer pageSize = (Integer) params.get("pageSize");
        AssertUtils.isNull(pageNumber, "当前页数不能为空");
        AssertUtils.isNull(pageSize, "分页大小不能为空");

        PageHelper.startPage(pageNumber, pageSize);
        PageInfo<UserDTO> list = userService.search(params);
        return list;
    }

    @Override
    @PostMapping(value = "/add")
    public UserDTO add(@RequestBody @Valid UserBO userBO) {
        User user = UserConverter.INSTANCE.bo2do(userBO);
        User savedUser = userService.addUser(user);
        UserDTO result = UserConverter.INSTANCE.do2dto(savedUser);
        return result;
    }

    @Override
    @PostMapping(value = "/edit")
    public int edit(@RequestBody @Valid UserBO userBO) {
        AssertUtils.isNull(userBO.getId(), "ID不能为空");
        User user = UserConverter.INSTANCE.bo2do(userBO);
        int editCnt = userService.editUser(user);
        return editCnt;
    }

    @Override
    @GetMapping(value = "/info/{id}")
    public UserDTO info(@PathVariable("id") Long id) {
        User user = userService.getById(id);
        UserDTO result = UserConverter.INSTANCE.do2dto(user);
        return result;
    }

}
