package cn.com.glsx.admin.modules;

import cn.com.glsx.admin.modules.user.service.UserService;
import com.glsx.plat.core.web.R;
import com.glsx.plat.web.controller.AbstractController;
import cn.com.glsx.vasp.modules.entity.User;
import org.apache.commons.lang.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author liuyf
 * @Title BaseController.java
 * @Package com.glsx.vasp.controller
 * @Description
 * @date 2019年10月24日 下午2:24:00
 */
@RestController
public class BaseController extends AbstractController {

    @Resource
    private UserService userService;

    /**
     * 这个给Spring Boot Admin探测用
     *
     * @return
     */
    @GetMapping(value = "/")
    public R index() {
        return R.ok("You get it!");
    }

    /**
     * 从session中获取当前用户
     *
     * @return
     */
    @Override
    public User getSessionUser() {
        User user = userService.getByToken();
        if (user == null || StringUtils.isBlank(user.getPhone())) {
            return null;
        }
        return user;
    }

    @Override
    public Long getUserId() {
        User user = getSessionUser();
        if (user != null) {
            return user.getId();
        }
        return null;
    }

    @Override
    public String getAccount() {
        User user = getSessionUser();
        if (user != null) {
            return user.getUsername();
        }
        return null;
    }

    /**
     * 参数校验
     *
     * @param bindingResult
     * @throws BindException
     */
    protected void checkDTOParams(BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }
    }

}
