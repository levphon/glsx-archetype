package cn.com.glsx.smartdevice.service;

import cn.com.glsx.kafka.config.MqConfig;
import cn.com.glsx.kafka.producer.ScenseProducer;
import cn.com.glsx.modules.model.param.SceneReq;
import cn.com.glsx.smartdevice.Common.Constants;
import cn.com.glsx.smartdevice.Common.EDay;
import cn.com.glsx.smartdevice.Common.ELogic;
import cn.com.glsx.smartdevice.factory.ConditionFactory;
import cn.com.glsx.smartdevice.factory.ConditionStrategy;
import cn.com.glsx.smartdevice.utils.DateUtils;
import cn.com.glsx.vasp.modules.entity.DeviceStatus;
import cn.com.glsx.vasp.modules.entity.ScenseData;
import cn.com.glsx.vasp.modules.mapper.DayInfoMapper;
import cn.com.glsx.vasp.modules.mapper.DeviceStatusMapper;
import cn.com.glsx.vasp.modules.mapper.ScenseDataMapper;
import com.alibaba.nacos.common.utils.CollectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author fengzhi
 * @version 1.0.0
 * @ClassName SceneComponent.java
 * @createTime 2020年03月12日 16:45:00
 */
@Slf4j
@Service
public class SceneService {
    @Autowired
    private ConditionFactory conditionFactory;

    @Autowired
    private DayInfoMapper dayInfoMapper;

    @Autowired
    private ScenseDataMapper scenseDataMapper;

    @Autowired
    private DeviceStatusMapper deviceStatusMapper;

    @Autowired
    private ScenseProducer scenseProducer;

    @Autowired
    private MqConfig config;

    //执行定时任务
    public void execute(Integer conditionType) {
        ConditionStrategy conditionStrategy = null;
        try {
            conditionStrategy = conditionFactory.getStrategy(conditionType);
        } catch (Exception e) {
            log.error("创建策略失败：" + e.getMessage());
        }

        if(null != conditionStrategy){
            long rangeBegintTime = System.currentTimeMillis();
            //判断当前日期，转为周期类型
            int executeCycle = getExecuteCycle();

            //a.1 第一类数据，处理符合周期类型，任意条件符合的场景,获取该类场景的触发条件
            List<ScenseData> dataAnyCondition = conditionStrategy.getScenseData(executeCycle);

            Map<Long, List<ScenseData>> scenseDataMap = listToMap(dataAnyCondition);

            ConditionStrategy finalConditionStrategy = conditionStrategy;
            scenseDataMap.entrySet().stream().forEach(entry -> {
                //每个场景的条件集合
                long scenseId = entry.getKey();
                List<ScenseData> dataList = entry.getValue();
                if (CollectionUtils.isNotEmpty(dataList)) {
                    //a.3 依照每个场景去判断是否条件任意满足(时间符合或者设备状态符合)

                    boolean isTrigger = finalConditionStrategy.isTrigger(dataList, rangeBegintTime, (ScenseData condition) -> getTriggerResult(rangeBegintTime, condition));

                    if (isTrigger) {
                        //如果符合触发场景,推送kafka
                        SceneReq scene = new SceneReq();
                        scene.setScenseId(scenseId);
                        scene.setType(conditionType);
                        scenseProducer.sendKafkaMessage(scene);
                    }
                }
            });
        }

    }
    //根据当前时间对应的执行周期类型
    private int getExecuteCycle() {
        //判断当天的日类型
        String date = DateUtils.getDate();
        //0表示工作日、为1节假日、为2双休日、3为调休日（上班）
        int dayCode = dayInfoMapper.getDayCodeByDate(date);
        //转换为对应的执行周期
        return EDay.getValueByCode(dayCode);
    }

    //将场景触发条件转为map,key为场景id，val为触发条件集合
    public Map<Long, List<ScenseData>> listToMap(List<ScenseData> dataAnyCondition) {
        //a.2 根据场景分类
        Map<Long, List<ScenseData>> scenseDataMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(dataAnyCondition)) {
            dataAnyCondition.stream().forEach(data -> {
                List<ScenseData> scenseDataList = null;
                long scenseId = data.getTemplateId();

                if (scenseDataMap.containsKey(scenseId)) {
                    scenseDataList = scenseDataMap.get(scenseId);
                } else {
                    scenseDataList = new ArrayList<>();
                }

                scenseDataList.add(data);
                scenseDataMap.put(scenseId, scenseDataList);
            });
        }
        return scenseDataMap;
    }

    //触发条件判断
    private boolean getTriggerResult(long rangeBegintTime, ScenseData condition) {
        String key = condition.getParamKey();
        String value = condition.getParamValue();

        //scene_condition_status设备状态
        if (Constants.CONDITION_STATUS.equals(key)) {
            //value为设备状态的条件数组
            if (StringUtils.isEmpty(value)) {
                return false;
            }
            log.info("getParamValue=="+value);
            String[] vals = value.split(",");
            //参数一，设备id
            String deviceId = vals[0];
            //参数二，设备指令code
            String deviceOrderCode = vals[1];
            //参数三,逻辑（1：<,2：>,3：=）
            int logic = Integer.parseInt(vals[2]);
            //参数四，值
            String deviceOrderValue = vals[3];

            DeviceStatus status = deviceStatusMapper.getByIdAndCode(deviceId,deviceOrderCode);
            if(null == status){
                log.info("设备状态不存在===deviceId："+deviceId+"===deviceOrderCode:"+deviceOrderCode);
                return false;
            }

            return ELogic.getValueByCode(logic,deviceOrderValue,status.getDeviceOrderValue());
        }

        //scene_condition_time指定时间
        if (Constants.CONDITION_TIME.equals(key)) {
            //value为时分
            if (StringUtils.isEmpty(value)) {
                return false;
            }

            //开始时间
            String begintTimeStr = DateUtils.getDate(new Date(rangeBegintTime), "HH:mm");
            //开始时间+预处理时间范围（可在配置中心修改）
            long rangeEndTime = rangeBegintTime + Long.parseLong(config.getTimeFuture());
            String endTimeStr = DateUtils.getDate(new Date(rangeEndTime), "HH:mm");
            boolean flag = value.equals(begintTimeStr) || value.equals(endTimeStr);
            return flag;
        }
        return false;
    }
}
