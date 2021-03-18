package cn.com.glsx.vasp.modules.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;

//todo name改为smart_device_status
@Accessors(chain = true)
@Data
@Entity
@Table(name = "smart_device_status")
public class DeviceStatus implements Serializable {
    @Id
    @Column(
            name = "id",
            unique = true,
            nullable = false
    )
    @GeneratedValue(
            strategy = GenerationType.IDENTITY,
            generator = "SELECT LAST_INSERT_ID()"
    )
    private Long id;

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
}