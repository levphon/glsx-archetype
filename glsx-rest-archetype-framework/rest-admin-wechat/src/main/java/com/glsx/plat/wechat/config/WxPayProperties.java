package com.glsx.plat.wechat.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ToString
@ConfigurationProperties(prefix = "wx.pay")
public class WxPayProperties {

    /**
     * appId
     */
    private String appId;

    /**
     * 商户号
     */
    private String mchId;

    /**
     * 商户密钥
     */
    private String mchKey;

    /**
     * 证书
     */
    private String keyPath;

    /**
     * 交易类型
     * <pre>
     * JSAPI--公众号支付
     * NATIVE--原生扫码支付
     * APP--app支付
     * </pre>
     */
    private String tradeType;

    private String signType;

    private String notifyUrl;

}
