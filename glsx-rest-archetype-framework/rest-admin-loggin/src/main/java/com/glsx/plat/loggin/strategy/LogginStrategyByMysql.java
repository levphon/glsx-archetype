package com.glsx.plat.loggin.strategy;

import com.glsx.plat.loggin.AbstractLogginStrategy;
import com.glsx.plat.loggin.entity.SysLogEntity;
import org.springframework.stereotype.Component;

@Component("mysql")
public class LogginStrategyByMysql extends AbstractLogginStrategy {

    @Override
    public void saveLog(SysLogEntity entity) {

    }

    @Override
    public void updateLogStatus(String logTraceId, String result) {

    }

}
