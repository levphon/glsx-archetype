package com.glsx.plat.ai.baidu.face.model;

import com.glsx.plat.ai.baidu.face.constant.FaceConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FaceUserDTO<T> implements Serializable {

    private String userId;

    private String groupId = FaceConstant.DEFAULT_GROUP_ID;

    private String faceToken;

    private T user;

}

