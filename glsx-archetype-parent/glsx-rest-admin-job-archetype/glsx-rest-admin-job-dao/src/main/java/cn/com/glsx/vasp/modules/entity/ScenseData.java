package cn.com.glsx.vasp.modules.entity;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.persistence.*;
import java.io.Serializable;

@Accessors(chain = true)
@Data
@Entity
@Table(name = "smart_scene_data")
public class ScenseData implements Serializable {
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
     * 场景ID 关联smart_scense中id
     */
    @Column(name = "template_id")
    private Long templateId;

    /**
     * 类型 1触发条件 2执行内容
     */
    private Byte type;

    /**
     * 排序
     */
    private Byte sort;

    /**
     * 参数key
     */
    @Column(name = "param_key")
    private String paramKey;

    /**
     * 参数值 数组对象
     */
    @Column(name = "param_value")
    private String paramValue;

    private static final long serialVersionUID = 1L;

    /**
     * 获取标识ID
     *
     * @return id - 标识ID
     */
    public Long getId() {
        return id;
    }

    /**
     * 设置标识ID
     *
     * @param id 标识ID
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * 获取场景ID 关联smart_scense中id
     *
     * @return template_id - 场景ID 关联smart_scense中id
     */
    public Long getTemplateId() {
        return templateId;
    }

    /**
     * 设置场景ID 关联smart_scense中id
     *
     * @param templateId 场景ID 关联smart_scense中id
     */
    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    /**
     * 获取类型 1触发条件 2执行内容
     *
     * @return type - 类型 1触发条件 2执行内容
     */
    public Byte getType() {
        return type;
    }

    /**
     * 设置类型 1触发条件 2执行内容
     *
     * @param type 类型 1触发条件 2执行内容
     */
    public void setType(Byte type) {
        this.type = type;
    }

    /**
     * 获取排序
     *
     * @return sort - 排序
     */
    public Byte getSort() {
        return sort;
    }

    /**
     * 设置排序
     *
     * @param sort 排序
     */
    public void setSort(Byte sort) {
        this.sort = sort;
    }

    /**
     * 获取参数key
     *
     * @return param_key - 参数key
     */
    public String getParamKey() {
        return paramKey;
    }

    /**
     * 设置参数key
     *
     * @param paramKey 参数key
     */
    public void setParamKey(String paramKey) {
        this.paramKey = paramKey == null ? null : paramKey.trim();
    }

    /**
     * 获取参数值 数组对象
     *
     * @return param_value - 参数值 数组对象
     */
    public String getParamValue() {
        return paramValue;
    }

    /**
     * 设置参数值 数组对象
     *
     * @param paramValue 参数值 数组对象
     */
    public void setParamValue(String paramValue) {
        this.paramValue = paramValue == null ? null : paramValue.trim();
    }
}