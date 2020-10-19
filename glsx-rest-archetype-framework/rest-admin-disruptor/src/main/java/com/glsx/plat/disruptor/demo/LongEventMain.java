package com.glsx.plat.disruptor.demo;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.nio.ByteBuffer;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @author: taoyr
 **/
public class LongEventMain {

    public static void main(String[] args) throws Exception {
        //创建线程池
        Executor executor = Executors.newCachedThreadPool();

        //事件工厂
        LongEventFactory factory = new LongEventFactory();

        //ringBuffer 的缓冲区的大小是1024  must be power of 2.
        int bufferSize = 1024;

        //创建一个disruptor ProducerType.MULTI:创建一个环形缓冲区支持多事件发布到一个环形缓冲区
        Disruptor<LongEvent> disruptor = new Disruptor<>(factory, bufferSize, executor, ProducerType.MULTI, new BlockingWaitStrategy());

        //创建一个消费者
        disruptor.handleEventsWith(new LongEventHandler());

        // Start the Disruptor, starts all threads running
        //启动并初始化disruptor
        disruptor.start();

        // Get the ring buffer from the Disruptor to be used for publishing.
        //获取已经初始化好的ringBuffer
        RingBuffer<LongEvent> ringBuffer = disruptor.getRingBuffer();

        LongEventProducer producer = new LongEventProducer(ringBuffer);

        ByteBuffer bb = ByteBuffer.allocate(8);
        for (long l = 0; true; l++) {
            //存入数据
            bb.putLong(0, l);
            producer.onData(bb);
            Thread.sleep(1000);
        }
    }

}
