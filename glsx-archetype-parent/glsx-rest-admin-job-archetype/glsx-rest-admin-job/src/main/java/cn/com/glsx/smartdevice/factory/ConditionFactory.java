package cn.com.glsx.smartdevice.factory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author fengzhi
 * @date 2021/3/9 17:05
 * @description 平台接口工厂类
 */
@Service
public class ConditionFactory {
    @Autowired
    Map<String, ConditionStrategy> strategys = new ConcurrentHashMap<>(2);

    public ConditionStrategy getStrategy(Integer code) throws Exception{
        ConditionStrategy strategy = strategys.get(Condition.getBeanNameByCode(code));
        if(strategy == null) {
            throw new RuntimeException("no strategy defined");
        }
        return strategy;
    }
}
