package cn.com.glsx.modules.model.param;

import lombok.Data;


@Data
public class SceneReq {
    //场景
    private Long scenseId;
    //1:任意条件触发 2：全部条件触发
    private Integer type;
}
