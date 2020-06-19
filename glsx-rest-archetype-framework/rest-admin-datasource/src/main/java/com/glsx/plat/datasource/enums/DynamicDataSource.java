package com.glsx.plat.datasource.enums;

import lombok.Getter;

@Getter
public enum DynamicDataSource {

    MASTER("master"),
    SLAVE("slave");

    private String dataSourceName;

    DynamicDataSource(String dataSourceName) {
        this.dataSourceName = dataSourceName;
    }

}
