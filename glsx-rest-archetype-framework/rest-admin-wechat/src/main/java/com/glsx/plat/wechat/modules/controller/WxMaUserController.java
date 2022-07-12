package com.glsx.plat.wechat.modules.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import com.binarywang.spring.starter.wxjava.miniapp.properties.WxMaProperties;
import com.glsx.plat.common.annotation.NoLogin;
import com.glsx.plat.common.annotation.SysLog;
import com.glsx.plat.common.enums.OperateType;
import com.glsx.plat.core.web.R;
import com.glsx.plat.wechat.config.WxMaConfiguration;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * 微信小程序用户接口
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Slf4j
@RestController
@RequestMapping("/wx/user")
public abstract class WxMaUserController {

    @Resource
    private WxMaProperties wxMaProperties;

    /**
     * 登陆接口
     *
     * @param code
     * @param codeForPhone
     * @return
     */
    @NoLogin
    @SysLog(module = "微信登录", action = OperateType.LOGIN, value = "小程序登录", saveLog = false)
    @ApiOperation("登录ByCode")
    @GetMapping(value = "/login")
    public R login(@RequestParam("code") String code, String codeForPhone) throws WxErrorException {
        if (StringUtils.isBlank(code)) return R.error("empty jscode");
        final WxMaService wxMaService = WxMaConfiguration.getMaService(wxMaProperties.getAppid());
        WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);

        //解密号码
        WxMaPhoneNumberInfo phoneNoInfo = null;
        if (StringUtils.isNotEmpty(codeForPhone) && !"undefined".equals(codeForPhone)) {
            phoneNoInfo = wxMaService.getUserService().getNewPhoneNoInfo(codeForPhone);
        }

        //增加自己的逻辑，关联相关数据
        Map<String, Object> rtnMap = cacheUser(session, phoneNoInfo);

        return R.ok().data(rtnMap);
    }

    /**
     * 关联登录用户到数据库、服务器端缓存用户信息等
     *
     * @param session
     * @param phoneNoInfo
     */
    protected abstract Map<String, Object> cacheUser(WxMaJscode2SessionResult session, WxMaPhoneNumberInfo phoneNoInfo);

}
