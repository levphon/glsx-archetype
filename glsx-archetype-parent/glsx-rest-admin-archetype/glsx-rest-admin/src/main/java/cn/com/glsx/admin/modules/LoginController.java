package cn.com.glsx.admin.modules;

import com.glsx.plat.common.annotation.SysLog;
import com.glsx.plat.core.web.R;
import cn.com.glsx.admin.modules.service.UserService;
import cn.com.glsx.admin.modules.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * @author payu
 */
@Slf4j
@RestController
public class LoginController {

    @Resource
    private UserService userService;

    /**
     * 登录
     */
    @SysLog
    @PostMapping(value = "/login")
    public R login(@RequestParam String phone, @RequestParam String code) {

//        User user = userService.findByPhone(phone);
        User user = new User();
        user.setPhone(phone);

        String token = userService.createToken(user);

        Map<String, Object> rtnMap = new HashMap<>();
        rtnMap.put("token", token);
        rtnMap.put("user", user);
        return R.ok().data(rtnMap);
    }

}
