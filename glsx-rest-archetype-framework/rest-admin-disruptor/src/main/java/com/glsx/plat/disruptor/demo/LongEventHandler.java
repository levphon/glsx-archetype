package com.glsx.plat.disruptor.demo;

import com.lmax.disruptor.EventHandler;

/**
 * @author: taoyr
 **/
public class LongEventHandler implements EventHandler<LongEvent> {

    //onEvent()方法是框架的回调用法
    public void onEvent(LongEvent event, long sequence, boolean endOfBatch) {
        System.out.println("Event: " + event);
    }

}
