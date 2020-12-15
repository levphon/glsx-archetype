package com.glsx.plat.ai.baidu.face.enums;

import lombok.Getter;

@Getter
public enum ErrorEnum {

    FACE_SERVICE_ERROR(216430, "rtse/face 服务异常"),
    VOICE_SERVICE_ERROR(216431, "语音识别服务异常"),
    VIDEO_SERVICE_CALL_FAIL(216432, "视频解析服务调用失败"),
    VIDEO_SERVICE_ERROR(216433, "视频解析服务发生错误"),
    LIVENESS_CHECK_FAIL(216434, "活体检测失败"),
    CODE_DIGIT_ERROR(216500, "验证码位数错误"),
    NOT_FOUND_FACE(216501, "没有找到人脸"),
    SESSION_LAPSE(216502, "当前会话已失效"),
    FOUND_MANY_FACES(216507, "视频中有多张人脸"),
    NOT_FOUND_VIDEO_INFO(216508, "没有找到视频信息"),
    VIDEO_QUALITY_TO_LOW(216908, "视频中人脸质量过低,请重新录制视频"),
    UNKNOWN_ERROR(2222222, "发生未知错误");

    private Integer errorCode;
    private String errorMsg;

    ErrorEnum(Integer errorCode, String errorMsg) {
        this.errorCode = errorCode;
        this.errorMsg = errorMsg;
    }

    public static ErrorEnum getInstance(Integer errorCode) {
        ErrorEnum[] errors = ErrorEnum.values();
        for (int i = 0; i < errors.length; i++) {
            if (errors[i].getErrorCode().equals(errorCode)) {
                return errors[i];
            }
        }
        return ErrorEnum.UNKNOWN_ERROR;
    }

}
