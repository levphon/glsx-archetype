package com.glsx.plat.loggin.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author payu
 */
@Data
@Component
@ConfigurationProperties("loggin")
public class LogginProperties {

    private String strategy;

}
