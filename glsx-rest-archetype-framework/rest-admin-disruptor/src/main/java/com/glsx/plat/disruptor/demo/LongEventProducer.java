package com.glsx.plat.disruptor.demo;

import com.lmax.disruptor.RingBuffer;

import java.nio.ByteBuffer;

/**
 * @author: taoyr
 **/
public class LongEventProducer {

    //环形缓冲区,装载生产好的数据；
    private final RingBuffer<LongEvent> ringBuffer;

    public LongEventProducer(RingBuffer<LongEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    //将数据推入到缓冲区的方法：将数据装载到ringBuffer
    public void onData(ByteBuffer bb) {
        // Grab the next sequence //获取下一个可用的序列号
        long sequence = ringBuffer.next();
        try {
            // Get the entry in the Disruptor //通过序列号获取空闲可用的LongEvent
            LongEvent event = ringBuffer.get(sequence);
            // Fill with data //设置数值
            event.set(bb.getLong(0));
        } finally {
            //数据发布，只有发布后的数据才会真正被消费者看见
            ringBuffer.publish(sequence);
        }
    }

}
