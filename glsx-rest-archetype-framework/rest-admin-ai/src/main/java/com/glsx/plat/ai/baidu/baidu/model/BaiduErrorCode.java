package com.glsx.plat.ai.baidu.baidu.model;

import com.glsx.plat.common.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

public class BaiduErrorCode {

    public static String getErrorMsg(String errorCode) {
        Map<String, String> map = new HashMap<>();
        map.put("rtse/face service error", "rtse/face");
        map.put("voice service error", "语音识别服务异常");
        map.put("video service call fail", "视频解析服务调用失败");
        map.put("video service error", "视频解析服务发生错误");
        map.put("liveness check fail", "活体检测失败");
        map.put("code digit error", "验证码位数错误");
        map.put("not found face", "没有找到人脸");
        map.put("session lapse", "当前会话已失效");
        map.put("redis connect error", "redis连接失败");
        map.put("redis operation error", "redis操作失败");
        map.put("found many faces", "视频中有多张人脸");
        map.put("not found video info", "没有找到视频信息");
        map.put("voice can not identify", "视频中的声音无法识别，请重新录制视频");
        map.put("视频中人脸质量较差", "视频中人脸质量过低,请重新录制视频");
        map.put("code length param error", "验证码长度错误");
        map.put("param[min_code_length] format error", "参数格式错误");
        map.put("param[max_code_length] format error", "参数格式错误");
        map.put("param[match_threshold] format error", "参数格式错误");
        String msg = map.get(errorCode);
        if (StringUtils.isNullOrEmpty(msg)) msg = "请重新尝试视频认证";
        return msg;
    }

}
