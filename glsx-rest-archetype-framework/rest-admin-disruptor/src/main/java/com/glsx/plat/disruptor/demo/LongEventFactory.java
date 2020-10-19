package com.glsx.plat.disruptor.demo;

import com.lmax.disruptor.EventFactory;

/**
 * @author: taoyr
 **/
public class LongEventFactory implements EventFactory<LongEvent> {

    public LongEvent newInstance() {
        return new LongEvent();
    }

}
