package com.glsx.plat.loggin;

import com.glsx.plat.loggin.properties.LogginProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LogginStrategyFactory {

    @Autowired
    private LogginProperties logginProperties;

    @Autowired
    Map<String, AbstractLogginStrategy> strategys = new ConcurrentHashMap<>(3);

    public AbstractLogginStrategy getStrategy() throws Exception {
        AbstractLogginStrategy strategy = strategys.get(logginProperties.getStrategy());
        if (strategy == null) {
            throw new RuntimeException("no strategy defined");
        }
        return strategy;
    }

}
