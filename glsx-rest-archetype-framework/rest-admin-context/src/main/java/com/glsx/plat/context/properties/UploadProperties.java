package com.glsx.plat.context.properties;

import com.glsx.plat.common.utils.StringUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author payu
 */
@Data
@Component
@ConfigurationProperties("upload")
public class UploadProperties {

    public String filepath;

    /**
     * 获取存放位置
     */
    private Map<String, String> location;

    public String getBasePath() {
        String location = "";
        String os = System.getProperty("os.name");
        if (this.getLocation() != null) {
            if (os.toLowerCase().startsWith("win")) {
                location = this.getLocation().get("windows");
            } else {
                location = this.getLocation().get("linux");
            }
        }
        return location;
    }

    public String getFilepath() {
        if (StringUtils.isEmpty(filepath)) filepath = "files";
        return "/" + filepath + "/";
    }

}
