package com.glsx.plat.context.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池配置
 */
@Slf4j
@EnableAsync
@Configuration
public class ThreadPoolConfig implements AsyncConfigurer {

    DefaultRejectedExecutionHandler rejectedExecutionHandler = new DefaultRejectedExecutionHandler();
    // 线程池维护线程的最少数量
    private int corePoolSize = 60;
    //最大线程池大小
    private int maxPoolSize = 300;
    //队列容量,排队数
    private int queueCapacity = 10000;

    @Bean(name = "threadPoolTaskExecutor")
    @Override
    public ThreadPoolTaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        //设置核心线程数
        threadPoolTaskExecutor.setCorePoolSize(corePoolSize);
        //设置最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(maxPoolSize);
        //设置队列容量
        threadPoolTaskExecutor.setQueueCapacity(queueCapacity);
        //设置线程活跃时间（秒） 最大允许空闲时间,超过时间并且当前池中线程数大于 corePoolSize 的时候会销毁
        threadPoolTaskExecutor.setKeepAliveSeconds(60);
        //设置默认线程名称
        threadPoolTaskExecutor.setThreadNamePrefix("payu-");
        //设置拒绝策略
//        threadPoolTaskExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);
        threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //等待所有任务结束后再关闭线程池
        threadPoolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);
        threadPoolTaskExecutor.initialize();
        return threadPoolTaskExecutor;
    }

    /**
     * 处理未捕获的异常
     *
     * @return
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        AsyncUncaughtExceptionHandler asyncUncaughtExceptionHandler = (throwable, method, objects) -> {
            log.error(method.getName() + " 执行异常", throwable);
            log.info("Exception message - " + throwable.getMessage());
            log.info("Method name - " + method.getName());
            for (Object param : objects) {
                log.info("Parameter value - " + param);
            }
        };
        return asyncUncaughtExceptionHandler;
    }

    /**
     * 线程池拒绝策略,等待一会(<斐波拉契数列:初始值为 100,100>),直到线程池空闲,然后再次提交线程;
     * 当线程池空闲时间过长(1 min)后,初始化等待时间
     * 当线程池过载,等待时间过长 (3 min) 后,抛出异常
     */
    class DefaultRejectedExecutionHandler implements RejectedExecutionHandler {

        // 上次等待时间
        private long lastWaitTime = 100;
        private long lastLastWaitTime = 100;
        private long maxWaitTime = DateUtils.MILLIS_PER_MINUTE * 3;
        private long maxIdleTime = DateUtils.MILLIS_PER_MINUTE;

        //最后一次池子不空闲时间
        private long lastPoolUnIdleTime = System.currentTimeMillis();

        @Override
        public void rejectedExecution(Runnable runnable, ThreadPoolExecutor executor) {
            if (System.currentTimeMillis() - lastPoolUnIdleTime >= maxIdleTime) {
                //如果池子空闲时间超过最大值,下次等待时就初始化为最开始等待时间
                lastWaitTime = 100;
                lastLastWaitTime = 100;
            }

            if (lastWaitTime >= maxWaitTime) {
                //如果上次等待时间超过 3 分钟 ,则需要增大线程池大小,并需要查询什么操作耗时过长
                log.error("上次等待时间[" + lastWaitTime + "]超过最大等待时间[" + maxWaitTime + "] ms,当前请求获取线程被拒绝;当前线程池配置 ==> \n" +
                        " 最大池大小[" + maxPoolSize + "],核心维护大小[" + corePoolSize + "],排队数[" + queueCapacity + "]");
                return;
            }

            //当前池子不是空闲的,记录池子最后不是空闲的时间
            lastPoolUnIdleTime = System.currentTimeMillis();

            //如果等待时间未超过最大等待时间,则以 <斐波拉契数列> 获取等待时间 200,300,500,800,1300...
            long nowWaitTime = lastWaitTime + lastLastWaitTime;

            try {
                //否则等待一段时间后重新提交线程
                Thread.sleep(nowWaitTime);
                executor.submit(runnable);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            lastLastWaitTime = lastWaitTime;
            lastWaitTime = nowWaitTime;
        }
    }
}
