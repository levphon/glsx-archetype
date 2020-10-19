package com.glsx.plat.disruptor.producer;

import lombok.Data;

import java.io.Serializable;

/**
 * @author: taoyr
 **/
@Data
public class NotifyEvent implements Serializable {

    private String message;

}
