package com.springboot.auth.authentication.entity;

import com.glsx.plat.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@NoArgsConstructor
@Entity
@Table(name = "d_resource")
public class Resource extends BaseEntity {

    private String code;
    private String name;
    private String type;
    private String url;
    private String method;
    private String description;

}
