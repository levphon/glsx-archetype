package cn.com.glsx.consumer.entity;

import lombok.Data;

import java.util.List;

/**
 * @author zhouhaibao
 * @date 2021/3/15 14:08
 */
@Data
public class DeviceStatus {

    private String devId;
    private String productKey;
    private List<DeviceStatusDetail> status;
}
