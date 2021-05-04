package com.glsx.plat.kafka.base;

import com.google.protobuf.ServiceException;
import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Cruise.Xu
 */
@Slf4j
public abstract class MessageBatchReceiver {

    protected ConsumerConfig consumerConfig;

    protected String topic;

    protected String nThreads;

    protected int consumerCount;

    /**
     * 最大消费间隔时间
     */
    protected int maxIntervalSeconds;

    protected kafka.javaapi.consumer.ConsumerConnector connector;

    protected Map<String, Integer> topicCountMap = new HashMap<>();

    public abstract void processMessage(List<byte[]> messageList) throws ServiceException;

    protected void init() {
        connector = Consumer.createJavaConsumerConnector(consumerConfig);
        int threadCnt = 1;
        if (null != nThreads && Integer.parseInt(nThreads) > 0) {
            threadCnt = Integer.parseInt(nThreads);
        }

        String[] topics = topic.split(",");
        for (int i = 0; i < topics.length; i++) {
            topicCountMap.put(topics[i], threadCnt);
        }

        final Map<String, List<KafkaStream<byte[], byte[]>>> streams = connector.createMessageStreams(topicCountMap);

        List<KafkaStream<byte[], byte[]>> list = new ArrayList<>();
        for (String ss : topics) {
            list.addAll(streams.get(ss));
        }

        final ExecutorService executor = Executors.newFixedThreadPool(threadCnt * topics.length);
        for (final KafkaStream<byte[], byte[]> kafkaStream : list) {
            executor.submit(() -> {
                ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
                List<byte[]> streamList = new ArrayList<>();
                while (it.hasNext()) {
                    byte[] message = it.next().message();
                    try {
                        // 接收到流并读取，转化为消息内容
                        streamList.add(message);
                        // 批量消费kafka中的数据
                        if (streamList.size() % consumerCount == 0) {
                            processMessage(streamList);
                            log.info("set before " + streamList.toString());
                            streamList.clear();
                            log.info("set after" + streamList.toString());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.error(e.getMessage());
                    }
                }
            });
        }

        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));
    }


    public void destroy() {
        if (null != connector) {
            connector.shutdown();
        }
    }

    public ConsumerConfig getConsumerConfig() {
        return consumerConfig;
    }

    public void setConsumerConfig(ConsumerConfig consumerConfig) {
        this.consumerConfig = consumerConfig;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic.trim().replaceAll(" ", "");
    }

    public String getnThreads() {
        return nThreads;
    }

    public void setnThreads(String nThreads) {
        this.nThreads = nThreads;
    }

    public int getConsumerCount() {
        return consumerCount;
    }

    public void setConsumerCount(int consumerCount) {
        this.consumerCount = consumerCount;
    }

    public int getMaxIntervalSeconds() {
        return maxIntervalSeconds;
    }

    public void setMaxIntervalSeconds(int maxIntervalSeconds) {
        this.maxIntervalSeconds = maxIntervalSeconds;
    }

}
