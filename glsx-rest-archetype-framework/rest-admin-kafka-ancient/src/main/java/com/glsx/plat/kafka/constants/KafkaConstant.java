package com.glsx.plat.kafka.constants;

/**
 * 全局变量
 */
public class KafkaConstant {
    /*****  kafka消费者配置属性 *****/

    //zookeeper地址
    public static final String ZOOKEEPER_CONNECT = "zookeeper.connect";

    //连接超时
    public static final String ZOOKEEPER_CONNECT_TIMEOUT = "zookeeper.connection.timeout.ms";

    //session超时
    public static final String ZOOKEEPER_SESSION_TIMEOUT = "zookeeper.session.timeout.ms";

    //指定默认消费者group id
    public static final String GROUP_ID = "group.id";

    //如果'enable.auto.commit'为true，则消费者偏移自动提交给Kafka的频率（以毫秒为单位），默认值为5000
    public static final String AUTO_COMMIT_INTERVAL = "auto.commit.interval.ms";

    //smallest和largest才有效，如果smallest重新0开始读取，如果是largest从logfile的offset读取。一般情况下我们都是设置smallest
    public static final String AUTO_OFFSET_RESET = "auto.offset.reset";

    public static final String REBLANCE_BACKOFF = "rebalance.backoff.ms";

    public static final String REBLANCE_MAX_RETRIES = "rebalance.max.retries";

}
