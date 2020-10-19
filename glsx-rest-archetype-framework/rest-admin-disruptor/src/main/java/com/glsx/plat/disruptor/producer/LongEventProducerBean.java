package com.glsx.plat.disruptor.producer;

import com.lmax.disruptor.RingBuffer;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

/**
 * @author: taoyr
 **/
@Component
public class LongEventProducerBean {

    //环形缓冲区,装载生产好的数据；
//    private final RingBuffer<NotifyEvent> ringBuffer;

//    public LongEventProducerBean(RingBuffer<NotifyEvent> ringBuffer) {
//        this.ringBuffer = ringBuffer;
//    }
//
//    //将数据推入到缓冲区的方法：将数据装载到ringBuffer
//    public void onData(ByteBuffer bb) {
//        // Grab the next sequence //获取下一个可用的序列号
//        long sequence = ringBuffer.next();
//        try {
//            // Get the entry in the Disruptor //通过序列号获取空闲可用的LongEvent
//            NotifyEvent event = ringBuffer.get(sequence);
//            // Fill with data //设置数值
////            event.setMessage(bb.getChar(0));
//        } finally {
//            //数据发布，只有发布后的数据才会真正被消费者看见
//            ringBuffer.publish(sequence);
//        }
//    }

}
