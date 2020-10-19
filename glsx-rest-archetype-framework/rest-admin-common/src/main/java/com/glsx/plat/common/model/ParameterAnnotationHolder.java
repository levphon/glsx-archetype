package com.glsx.plat.common.model;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author: taoyr
 **/
@Data
@Accessors(chain = true)
public class ParameterAnnotationHolder implements Serializable {

    /**
     * 1=参数前注解
     *
     * 2=参数实体字段注解
     */
    private Integer type;

    private String parameterName;

    private String fieldName;

}
