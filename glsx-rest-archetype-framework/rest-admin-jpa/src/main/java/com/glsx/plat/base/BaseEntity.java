package com.glsx.plat.base;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.glsx.plat.core.enums.SysConstants;
import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Accessors(chain = true)
@Data
@MappedSuperclass
public abstract class BaseEntity implements Serializable {

    private static final long serialVersionUID = -8402343759691572908L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    protected Long id;

    /**
     * 删除状态：0未删除，-1删除，默认未删除
     */
    @Column(name = "del_flag", length = 2)
    private Integer delFlag = SysConstants.DeleteStatus.normal.getCode();

    /**
     * 启用状态：0初始，1启用，2停用，默认启用
     */
    @Column(name = "enable_status", length = 2)
    private Integer enableStatus = SysConstants.EnableStatus.enable.getCode();

    /**
     * 创建人
     */
    @Column(name = "created_by", length = 10)
    private Integer createBy;

    /**
     * 操作者
     */
    @Column(name = "updated_by", length = 10)
    private Integer updateBy;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "created_date", length = 19, updatable = false)
    private Date createDate = new Date();

    /**
     * 更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Column(name = "updated_date", length = 19)
    private Date updateDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;

        BaseEntity that = (BaseEntity) o;

        return getId() != null ? getId().equals(that.getId()) : that.getId() == null;
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }

}
