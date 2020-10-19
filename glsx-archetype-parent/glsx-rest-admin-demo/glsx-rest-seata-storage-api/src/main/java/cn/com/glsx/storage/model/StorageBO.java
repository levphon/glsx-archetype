package cn.com.glsx.storage.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class StorageBO implements Serializable {

    private Long id;

    private String commodityCode;

    private String name;

    private Integer count;

}
