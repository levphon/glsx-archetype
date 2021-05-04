package com.glsx.plat.kafka.base;

import kafka.consumer.Consumer;
import kafka.consumer.ConsumerConfig;
import kafka.consumer.ConsumerIterator;
import kafka.consumer.KafkaStream;
import kafka.message.MessageAndMetadata;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 消费基类
 */
@Slf4j
public abstract class MessageReceiver {

    protected ConsumerConfig consumerConfig;

    protected String topic;

    protected Integer nThreads;

    protected kafka.javaapi.consumer.ConsumerConnector connector;

    protected Map<String, Integer> topicCountMap = new HashMap<>();

    public abstract void processMessage(String topic, byte[] message) throws Exception;

    protected void init() {

        connector = Consumer.createJavaConsumerConnector(consumerConfig);
        int threadCnt = 1;
        if (null != nThreads && nThreads > 0) {
            threadCnt = nThreads;
        }

        String[] topics = topic.split(",");
        for (int i = 0; i < topics.length; i++) {
            topicCountMap.put(topics[i], threadCnt);
        }

        Map<String, List<KafkaStream<byte[], byte[]>>> streams = connector.createMessageStreams(topicCountMap);

        List<KafkaStream<byte[], byte[]>> list = new ArrayList<>();
        for (String ss : topics) {
            list.addAll(streams.get(ss));
        }

        final ExecutorService executor = Executors.newFixedThreadPool(threadCnt * topics.length);
        for (final KafkaStream<byte[], byte[]> kafkaStream : list) {
            executor.submit(() -> {
                ConsumerIterator<byte[], byte[]> it = kafkaStream.iterator();
                while (it.hasNext()) {
                    MessageAndMetadata<byte[], byte[]> item = it.next();
                    byte[] message = item.message();
                    try {
                        processMessage(item.topic(), message);
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
        if (null != connector)
            connector.shutdown();
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

    public Integer getnThreads() {
        return nThreads;
    }

    public void setnThreads(Integer nThreads) {
        this.nThreads = nThreads;
    }
}
