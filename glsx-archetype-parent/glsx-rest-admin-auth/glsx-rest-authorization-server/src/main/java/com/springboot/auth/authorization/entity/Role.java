package com.springboot.auth.authorization.entity;

import com.glsx.plat.mybatis.base.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Role extends BaseEntity {
    private String code;
    private String name;
    private String description;
}
