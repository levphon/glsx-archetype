package cn.com.glsx.order.model;

import lombok.Data;

@Data
public class OrderBO {

    private String userId;

    private String commodityCode;

    private Integer orderCount;

}
