package cn.com.glsx.smartdevice.factory;

import cn.com.glsx.smartdevice.service.TriggerListener;
import cn.com.glsx.vasp.modules.entity.ScenseData;

import java.util.List;

/**
 * @author fengzhi
 * @date 2021/3/9 17:05
 * @description 平台统一对外接口
 */
public interface ConditionStrategy {
    List<ScenseData> getScenseData(int executeCycle);

    boolean isTrigger(List<ScenseData> dataList, long rangeBegintTime, TriggerListener trigger);
}
