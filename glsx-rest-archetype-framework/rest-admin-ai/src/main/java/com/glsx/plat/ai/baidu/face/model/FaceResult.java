package com.glsx.plat.ai.baidu.face.model;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FaceResult implements Serializable {

    @JSONField(name = "log_id")
    private String logId;

    private int cached;

    @JSONField(name = "error_code")
    private Integer errorCode;

    @JSONField(name = "error_msg")
    private String errorMsg;

    private long timestamp;

    private JSONObject data;

    public boolean isSuccess() {
        return "0".equals(String.valueOf(this.errorCode));
    }

}


