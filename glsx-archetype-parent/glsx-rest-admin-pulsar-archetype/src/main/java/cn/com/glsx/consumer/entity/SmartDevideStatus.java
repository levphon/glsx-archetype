package cn.com.glsx.consumer.entity;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

@Table(name = "smart_devide_status")
public class SmartDevideStatus implements Serializable {
    @Id
    private Integer id;

    /**
     * 设备id
     */
    @Column(name = "device_id")
    private String deviceId;

    /**
     * 设备指令
     */
    @Column(name = "device_order_code")
    private String deviceOrderCode;

    /**
     * 设备指令值
     */
    @Column(name = "device_order_value")
    private String deviceOrderValue;

    /**
     * 创建时间
     */
    private Date createtime;

    /**
     * 更新时间
     */
    private Date updatetime;

    /**
     * 产品id
     */
    @Column(name = "product_key")
    private String productKey;

    private static final long serialVersionUID = 1L;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取设备id
     *
     * @return device_id - 设备id
     */
    public String getDeviceId() {
        return deviceId;
    }

    /**
     * 设置设备id
     *
     * @param deviceId 设备id
     */
    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId == null ? null : deviceId.trim();
    }


    /**
     * 获取设备指令
     *
     * @return device_order_code - 设备指令
     */
    public String getDeviceOrderCode() {
        return deviceOrderCode;
    }

    /**
     * 设置设备指令
     *
     * @param deviceOrderCode 设备指令
     */
    public void setDeviceOrderCode(String deviceOrderCode) {
        this.deviceOrderCode = deviceOrderCode == null ? null : deviceOrderCode.trim();
    }

    /**
     * 获取设备指令值
     *
     * @return device_order_value - 设备指令值
     */
    public String getDeviceOrderValue() {
        return deviceOrderValue;
    }

    /**
     * 设置设备指令值
     *
     * @param deviceOrderValue 设备指令值
     */
    public void setDeviceOrderValue(String deviceOrderValue) {
        this.deviceOrderValue = deviceOrderValue == null ? null : deviceOrderValue.trim();
    }

    /**
     * 获取创建时间
     *
     * @return createtime - 创建时间
     */
    public Date getCreatetime() {
        return createtime;
    }

    /**
     * 设置创建时间
     *
     * @param createtime 创建时间
     */
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    /**
     * 获取更新时间
     *
     * @return updatetime - 更新时间
     */
    public Date getUpdatetime() {
        return updatetime;
    }

    /**
     * 设置更新时间
     *
     * @param updatetime 更新时间
     */
    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    /**
     * 获取产品id
     *
     * @return product_key - 产品id
     */
    public String getProductKey() {
        return productKey;
    }

    /**
     * 设置产品id
     *
     * @param productKey 产品id
     */
    public void setProductKey(String productKey) {
        this.productKey = productKey == null ? null : productKey.trim();
    }

}