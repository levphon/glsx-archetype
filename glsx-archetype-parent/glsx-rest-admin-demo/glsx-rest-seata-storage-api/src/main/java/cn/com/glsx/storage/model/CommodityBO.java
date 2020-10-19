package cn.com.glsx.storage.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 商品信息
 */
@Data
public class CommodityBO implements Serializable {

    private Long id;

    private String commodityCode;

    private String name;

    private Integer count;

}
