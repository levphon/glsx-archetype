package cn.com.glsx.consumer.service;

import cn.com.glsx.consumer.entity.DeviceStatus;
import cn.com.glsx.consumer.entity.DeviceStatusDetail;
import cn.com.glsx.consumer.entity.SmartDevideStatus;
import cn.com.glsx.consumer.mapper.SmartDevideStatusMapper;
import cn.com.glsx.consumer.utils.DateUtils;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.jetbrains.annotations.TestOnly;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.security.ssl.Record;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author zhouhaibao
 * @date 2021/3/15 14:24
 */
@Service
@Slf4j
public class TuyaConmsumerService {

    @Autowired
    private SmartDevideStatusMapper smartDevideStatusMapper;

    /**
     * 处理消息
     *
     * @param data
     */
    public void processMessage(String data) {
        DeviceStatus deviceStatus = JSONObject.parseObject(data, DeviceStatus.class);
        List<SmartDevideStatus> list = buildParams(deviceStatus);
        if(CollectionUtils.isNotEmpty(list)){
            list.forEach(index->{
                if(index.getId()==null){
                    smartDevideStatusMapper.insert(index);
                }else {
                    smartDevideStatusMapper.updateByPrimaryKeySelective(index);
                }
            });
        }
    }

    private List<SmartDevideStatus> buildParams(DeviceStatus deviceStatus) {
        List<SmartDevideStatus> list = new ArrayList<>();
        List<DeviceStatusDetail> status = deviceStatus.getStatus();
        if (CollectionUtils.isNotEmpty(status)) {
            list = status.stream().map(index -> {
                SmartDevideStatus smartDevideStatus=smartDevideStatusMapper.findDevStatusByIdAndCode(deviceStatus.getDevId(),index.getCode());
                if(smartDevideStatus==null) {
                    smartDevideStatus=new SmartDevideStatus();
                    smartDevideStatus.setDeviceId(deviceStatus.getDevId());
                    smartDevideStatus.setProductKey(deviceStatus.getProductKey());
                    smartDevideStatus.setCreatetime(new Date(index.getT()));
                    smartDevideStatus.setUpdatetime(new Date(index.getT()));
                    smartDevideStatus.setDeviceOrderCode(index.getCode());
                }
                smartDevideStatus.setUpdatetime(new Date(index.getT()));
                smartDevideStatus.setDeviceOrderValue(index.getValue());
                return smartDevideStatus;
            }).collect(Collectors.toList());
        }
        return list;
    }
}
