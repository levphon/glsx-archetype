package com.glsx.plat.loggin.strategy;

import com.glsx.plat.loggin.AbstractLogginStrategy;
import com.glsx.plat.loggin.entity.SysLogEntity;
import com.glsx.plat.loggin.mapper.SysLogMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component("mysql")
public class LogginStrategyByMysql extends AbstractLogginStrategy {

    @Resource
    private SysLogMapper sysLogMapper;

    @Override
    public void saveLog(SysLogEntity entity) {
        sysLogMapper.insert(entity);
    }

    @Override
    public void updateLogStatus(String logTraceId, String result) {
        sysLogMapper.updateByLogId(logTraceId, result);
    }

}
