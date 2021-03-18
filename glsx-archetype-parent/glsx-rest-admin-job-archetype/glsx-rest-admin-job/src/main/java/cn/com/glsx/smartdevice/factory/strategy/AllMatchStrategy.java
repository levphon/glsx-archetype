package cn.com.glsx.smartdevice.factory.strategy;

import cn.com.glsx.kafka.producer.ScenseProducer;
import cn.com.glsx.smartdevice.factory.ConditionStrategy;
import cn.com.glsx.smartdevice.service.TriggerListener;
import cn.com.glsx.vasp.modules.entity.ScenseData;
import cn.com.glsx.vasp.modules.mapper.ScenseDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class AllMatchStrategy implements ConditionStrategy {
    @Autowired
    private ScenseDataMapper scenseDataMapper;

    @Autowired
    private ScenseProducer scenseProducer;

    @Override
    public List<ScenseData> getScenseData(int executeCycle) {
        return scenseDataMapper.getScenseAllCondition(executeCycle);
    }

    @Override
    public boolean isTrigger(List<ScenseData> dataList, long rangeBegintTime, TriggerListener trigger) {
        return dataList.stream().allMatch(condition -> trigger.getFlag(condition));
    }
}
