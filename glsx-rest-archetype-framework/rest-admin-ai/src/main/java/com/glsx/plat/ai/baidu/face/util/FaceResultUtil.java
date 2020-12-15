package com.glsx.plat.ai.baidu.face.util;

import com.glsx.plat.ai.baidu.face.constant.FaceConstant;
import com.glsx.plat.ai.baidu.face.enums.ErrorEnum;
import com.glsx.plat.ai.baidu.face.model.FaceResult;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

@Slf4j(topic = "百度API接口请求结果解析")
public class FaceResultUtil {

    public static FaceResult isSuccess(JSONObject res) {
        FaceResult result = parseJsonObject(res);
        if (!result.isSuccess()) {
            // 对错误进行分类
            ErrorEnum errorEnum = ErrorEnum.getInstance(result.getErrorCode());
            if (errorEnum == null) {
//                throw new BizException("百度接口请求失败" + result.getErrorMsg());
            } else {
//                throw new BizException(result.getErrorCode());
            }
            log.error("抛出异常{}", result.getErrorMsg());
        }
        return result;
    }

    /**
     * 解析JsonObject
     *
     * @return
     */
    private static FaceResult parseJsonObject(JSONObject res) {
        FaceResult faceResult = FaceResult.builder().build();
        try {
            String logId = res.has(FaceConstant.LOG_ID) ? res.get(FaceConstant.LOG_ID).toString() : "0";
            Integer errorCode = res.has(FaceConstant.ERROR_CODE) ? res.getInt(FaceConstant.ERROR_CODE) : -1;
            String errorMsg = res.has(FaceConstant.ERROR_MSG) ? res.getString(FaceConstant.ERROR_MSG) : "";
            int cached = res.has(FaceConstant.CACHED) ? res.getInt(FaceConstant.CACHED) : 0;
            long timestamp = res.has(FaceConstant.TIMESTAMP) ? res.getLong(FaceConstant.TIMESTAMP) : 0;
            Object dataString = res.has(FaceConstant.RESULT) ? res.get(FaceConstant.RESULT) : "";
            com.alibaba.fastjson.JSONObject data = null;
            if (dataString != null) {
                data = com.alibaba.fastjson.JSONObject.parseObject(dataString.toString());
            }
            faceResult.setLogId(logId);
            faceResult.setErrorCode(errorCode);
            faceResult.setErrorMsg(errorMsg);
            faceResult.setCached(cached);
            faceResult.setTimestamp(timestamp);
            faceResult.setData(data);
        } catch (Exception e) {
            log.error("JSONObject解析失败", e);
        }
        return faceResult;
    }

}


