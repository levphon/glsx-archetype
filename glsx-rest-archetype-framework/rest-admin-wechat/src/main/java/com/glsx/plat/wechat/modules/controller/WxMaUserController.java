package com.glsx.plat.wechat.modules.controller;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import cn.binarywang.wx.miniapp.bean.WxMaPhoneNumberInfo;
import cn.binarywang.wx.miniapp.bean.WxMaUserInfo;
import com.glsx.plat.common.annotation.NoLogin;
import com.glsx.plat.common.annotation.SysLog;
import com.glsx.plat.core.web.R;
import com.glsx.plat.wechat.config.WxMaConfiguration;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 微信小程序用户接口
 *
 * @author <a href="https://github.com/binarywang">Binary Wang</a>
 */
@Slf4j
@RestController
@RequestMapping("/wx/user/{appid}")
public abstract class WxMaUserController {

    /**
     * 登陆接口
     *
     * @param appid
     * @param code
     * @param encryptedData
     * @param iv
     * @return
     */
    @SysLog
    @NoLogin
    @ApiOperation("登录ByCode")
    @GetMapping(value = "/login")
    public R login(@PathVariable String appid, @RequestParam("code") String code, String encryptedData, String iv) throws WxErrorException {
        if (StringUtils.isBlank(code)) return R.error("empty jscode");

        final WxMaService wxMaService = WxMaConfiguration.getMaService(appid);
        WxMaJscode2SessionResult session = wxMaService.getUserService().getSessionInfo(code);
        log.info(session.toString());

        //解密号码
        WxMaPhoneNumberInfo phoneNoInfo = wxMaService.getUserService().getPhoneNoInfo(session.getSessionKey(), encryptedData, iv);

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

    /**
     * <pre>
     * 获取用户信息接口
     * </pre>
     */
    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public R info(@PathVariable String appid,
                  String signature, String rawData, String encryptedData, String iv) {

        String sessionKey = "";

        final WxMaService wxMaService = WxMaConfiguration.getMaService(appid);

        // 解密用户信息
        WxMaUserInfo userInfo = wxMaService.getUserService().getUserInfo(sessionKey, encryptedData, iv);

        linkUser(userInfo);

        return R.ok().data(userInfo);
    }

    /**
     * 关联登录用户到数据库、服务器端缓存用户信息等
     *
     * @param userInfo
     */
    protected abstract Map<String, Object> linkUser(WxMaUserInfo userInfo);

}
