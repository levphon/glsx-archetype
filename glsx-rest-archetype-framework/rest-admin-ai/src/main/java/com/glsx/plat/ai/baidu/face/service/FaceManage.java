package com.glsx.plat.ai.baidu.face.service;

import com.alibaba.fastjson.JSON;
import com.baidu.aip.face.FaceVerifyRequest;
import com.baidu.aip.face.MatchRequest;
import com.glsx.plat.ai.baidu.face.FaceConfig;
import com.glsx.plat.ai.baidu.face.constant.FaceConstant;
import com.glsx.plat.ai.baidu.face.enums.ActionTypeEnum;
import com.glsx.plat.ai.baidu.face.enums.LivenessControlEnum;
import com.glsx.plat.ai.baidu.face.enums.QualityControlEnum;
import com.glsx.plat.ai.baidu.face.model.FaceResult;
import com.glsx.plat.ai.baidu.face.model.FaceUserDTO;
import com.glsx.plat.ai.baidu.face.model.ImageU;
import com.glsx.plat.ai.baidu.face.util.FaceResultUtil;
import com.glsx.plat.ai.baidu.face.util.FaceUtil;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

/**
 * Desc: 人脸识别相关服务
 */
@Slf4j
@Component
public class FaceManage {

    @Autowired
    private FaceConfig config;

    @Autowired
    private FaceUtil faceUtil;

    /**
     * 人脸注册
     */
    public FaceResult faceRegister(FaceUserDTO userDTO, ImageU imageU) {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<>();
        // 用户资料
        options.put("user_info", JSON.toJSONString(userDTO));
        // 图片质量
        options.put("quality_control", QualityControlEnum.LOW.name());
        // 活体检测控制
        options.put("liveness_control", LivenessControlEnum.NONE.name());
        // 操作方式
        options.put("action_type", ActionTypeEnum.REPLACE.name());

        String image = imageU.getData();
        String imageType = imageU.getImageTypeEnum().name();
        String groupId = userDTO.getGroupId();
        String userId = userDTO.getUserId();

        // 人脸注册
        JSONObject res = faceUtil.getClient().addUser(image, imageType, groupId, userId, options);
        FaceResult faceResult = FaceResultUtil.isSuccess(res);
        log.info("人脸注册成功");
        return faceResult;
    }

    /**
     * 人脸更新
     */
    public void faceUpdate(FaceUserDTO userDTO, ImageU imageU) {
        HashMap<String, String> options = new HashMap<>();
        // 用户资料
        options.put("user_info", JSON.toJSONString(userDTO));
        // 图片质量
        options.put("quality_control", QualityControlEnum.LOW.name());
        // 活体检测控制
        options.put("liveness_control", LivenessControlEnum.NONE.name());
        // 操作方式
        options.put("action_type", ActionTypeEnum.REPLACE.name());

        String image = imageU.getData();
        String imageType = imageU.getImageTypeEnum().name();
        String groupId = userDTO.getGroupId();
        String userId = userDTO.getUserId();

        // 人脸更新
        JSONObject res = faceUtil.getClient().updateUser(image, imageType, groupId, userId, options);
        FaceResultUtil.isSuccess(res);
        log.info("人脸更新成功 {}", res.toString(2));
    }


    /**
     * 人脸删除
     */
    public void faceDelete(String userId, String groupId, String faceToken) {
        // 传入可选参数调用接口
        HashMap<String, String> options = new HashMap<>();
        // 人脸删除
        JSONObject res = faceUtil.getClient().faceDelete(userId, groupId, faceToken, options);
        FaceResultUtil.isSuccess(res);
        log.info("人脸删除成功 {}", res.toString(2));
    }


    /**
     * 用户信息查询
     */
    public FaceUserDTO<String> findUser(String userId, String groupId) {
        HashMap<String, String> options = new HashMap<>();
        // 用户信息查询
        JSONObject res = faceUtil.getClient().getUser(userId, groupId, options);
        FaceResult result = FaceResultUtil.isSuccess(res);
        return JSON.parseObject(result.getData().toJSONString(), FaceUserDTO.class);
    }


    /**
     * 获取用户人脸列表
     *
     * @throws Exception
     */
    public FaceResult faceGetList(String userId, String groupId) {
        HashMap<String, String> options = new HashMap<>();
        // 获取用户人脸列表
        JSONObject res = faceUtil.getClient().faceGetlist(userId, groupId, options);
        return FaceResultUtil.isSuccess(res);
    }


    /**
     * 获取用户列表
     */
    public FaceResult listUserByGroupId(String groupId) {
        HashMap<String, String> options = new HashMap<>();
        options.put("start", "0");
        options.put("length", "50");
        // 获取用户列表
        JSONObject res = faceUtil.getClient().getGroupUsers(groupId, options);
        return FaceResultUtil.isSuccess(res);
    }


    /**
     * 删除用户
     */
    public void deleteUser(String userId, String groupId) {
        HashMap<String, String> options = new HashMap<>();
        // 删除用户
        JSONObject res = faceUtil.getClient().deleteUser(groupId, userId, options);
        FaceResultUtil.isSuccess(res);
        log.info("用户删除成功 {}", res.toString(2));
    }


    /**
     * 创建用户组
     */
    public void addGroup(String groupId) {
        HashMap<String, String> options = new HashMap<>();
        // 创建用户组
        JSONObject res = faceUtil.getClient().groupAdd(groupId, options);
        FaceResultUtil.isSuccess(res);
        log.info("创建用户组 {}", res.toString(2));
    }


    /**
     * 删除用户组
     */
    public void deleteGroup(String groupId) {
        HashMap<String, String> options = new HashMap<>();
        // 删除用户组
        JSONObject res = faceUtil.getClient().groupDelete(groupId, options);
        FaceResultUtil.isSuccess(res);
        log.info("删除用户组 {}", res.toString(2));
    }

    /**
     * 组列表查询
     */
    public FaceResult listGroup() {
        HashMap<String, String> options = new HashMap<>();
        options.put("start", "0");
        options.put("length", "50");
        // 组列表查询
        JSONObject res = faceUtil.getClient().getGroupList(options);
        return FaceResultUtil.isSuccess(res);
    }


    /**
     * 身份验证（没权限使用）
     */
    public FaceResult personVerify(String idCardNumber, String realName, ImageU imageU) {
        HashMap<String, String> options = new HashMap<>();
        options.put("quality_control", QualityControlEnum.LOW.name());
        options.put("liveness_control", LivenessControlEnum.NONE.name());
        // 身份验证
        JSONObject res = faceUtil.getClient().personVerify(imageU.getData(), imageU.getImageTypeEnum().name(), idCardNumber, realName, options);
        return FaceResultUtil.isSuccess(res);
    }

    /**
     * 人脸对比
     */
    public int faceMatchScore(ImageU imageU1, ImageU imageU2) {
        MatchRequest req1 = new MatchRequest(imageU1.getData(), imageU1.getImageTypeEnum().name());
        MatchRequest req2 = new MatchRequest(imageU2.getData(), imageU2.getImageTypeEnum().name());
        ArrayList<MatchRequest> requests = new ArrayList<>();
        requests.add(req1);
        requests.add(req2);
        JSONObject res = faceUtil.getClient().match(requests);
        FaceResult result = FaceResultUtil.isSuccess(res);
        if (result.isSuccess()) {
            // 对结果进行特殊处理
            Integer score = result.getData().getInteger(FaceConstant.SCORE);
            return score == null ? 0 : score;
        } else {
            log.error("人脸对比错误(错误码{}) {}", result.getErrorCode(), result.getErrorMsg());
            return -1;
        }
    }

    /**
     * 人脸是否对比成功
     *
     * @param imageU1
     * @param imageU2
     * @param score   匹配分数
     * @return
     */
    public boolean isFaceMatch(ImageU imageU1, ImageU imageU2, Integer score) {
        int defaultScore = config.getFaceMatchScore();
        if (Objects.nonNull(score)) {
            defaultScore = score;
        }
        return faceMatchScore(imageU1, imageU2) > defaultScore;
    }

    /**
     * 人脸检测
     */
    public FaceResult faceDetect(ImageU imageU) {
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("face_field", "age");
        options.put("max_face_num", "2");
        options.put("face_type", "LIVE");
        // 人脸检测
        JSONObject res = faceUtil.getClient().detect(imageU.getData(), imageU.getImageTypeEnum().name(), options);
        return FaceResultUtil.isSuccess(res);
    }


    /**
     * 人脸搜索
     */
    public FaceResult faceSearch(String groupIds, ImageU imageU) {
        HashMap<String, String> options = new HashMap<String, String>();
        options.put("max_face_num", "1");
        options.put("max_user_num", "1");
        options.put("quality_control", QualityControlEnum.LOW.name());
        options.put("liveness_control", LivenessControlEnum.NONE.name());
        // 人脸搜索
        JSONObject res = faceUtil.getClient().search(imageU.getData(), imageU.getImageTypeEnum().name(), groupIds, options);
        return FaceResultUtil.isSuccess(res);
    }

    /**
     * 活体检测
     */
    public FaceResult faceVerify(ImageU imageU) {
        FaceVerifyRequest req = new FaceVerifyRequest(imageU.getData(), imageU.getImageTypeEnum().name());
        ArrayList<FaceVerifyRequest> list = new ArrayList<>();
        list.add(req);
        JSONObject res = faceUtil.getClient().faceverify(list);
        return FaceResultUtil.isSuccess(res);
    }

}


