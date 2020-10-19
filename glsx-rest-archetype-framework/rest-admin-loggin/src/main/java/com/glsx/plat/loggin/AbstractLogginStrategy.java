package com.glsx.plat.loggin;

import com.glsx.plat.loggin.entity.SysLogEntity;

public abstract class AbstractLogginStrategy {

    /**
     * 记录日志
     *
     * @param entity
     */
    public abstract void saveLog(SysLogEntity entity);

    /**
     * 更新状态，只有成功才更新，没更新当失败处理
     *
     * @param logTraceId
     * @param result
     */
    public abstract void updateLogStatus(String logTraceId, String result);

}
