package cn.com.glsx.wechat.modules.controller;

import com.glsx.plat.common.annotation.NoLogin;
import com.glsx.plat.core.web.R;
import com.glsx.plat.wechat.common.config.WxMaProperties;
import com.glsx.plat.wechat.common.config.WxMpProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Set;

@RestController
@RequestMapping("/wx/config/")
public class WxConfigController {

    @Resource
    private WxMpProperties wxMpProperties;

    @Resource
    private WxMaProperties wxMaProperties;

    /**
     * @param type MP公众号 MiniApp小程序
     * @return
     */
    @GetMapping("/get")
    public R get(String type) {
        if (StringUtils.isBlank(type)) return R.error("empty type");

        Set<String> appidSet = null;
//        if (WxType.MP.name().equals(type)) {
//            appidSet = wxMpProperties.getConfigs();
//        } else if (WxType.MiniApp.name().equals(type)) {
//            appidSet = wxMaProperties.getConfigs();
//        }
        return R.ok().data(appidSet);
    }

    /**
     * @param idx MP公众号
     * @return
     */
    @NoLogin
    @GetMapping("/get/mp/{idx}")
    public R getMpAppid(@PathVariable("idx") Integer idx) {
        if (idx == null || idx < 0) return R.error("error index");
        // TODO: 2020/5/26  越界检查
        String appid = wxMpProperties.getConfigs().get(idx).getAppId();
        return R.ok().data(appid);
    }

    /**
     * @param idx MiniApp小程序
     * @return
     */
    @NoLogin
    @GetMapping("/get/ma/{idx}")
    public R getMaAppid(@PathVariable("idx") Integer idx) {
        if (idx == null || idx < 0) return R.error("error index");
        // TODO: 2020/5/26  越界检查
        String appid = wxMaProperties.getConfigs().get(idx).getAppid();
        return R.ok().data(appid);
    }

}
