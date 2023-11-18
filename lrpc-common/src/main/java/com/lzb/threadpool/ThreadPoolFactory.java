package com.lzb.threadpool;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 创建 ThreadPool(线程池) 的工厂.
 */
@Slf4j
public final class ThreadPoolFactory {


    private static final Map<String, ExecutorService> THREAD_POOLS = new ConcurrentHashMap<>();

    private ThreadPoolFactory() {
    }

    public static ExecutorService createThreadPoolIfAbsent(ThreadPoolConfig threadPoolConfig, String threadNamePrefix) {
        ExecutorService threadPool = THREAD_POOLS.computeIfAbsent(threadNamePrefix, k -> createThreadPool(threadPoolConfig, threadNamePrefix));
        // 如果 threadPool 被 shutdown 的话就重新创建一个
        if (threadPool.isShutdown() || threadPool.isTerminated()) {
            THREAD_POOLS.remove(threadNamePrefix);
            threadPool = createThreadPool(threadPoolConfig, threadNamePrefix);
            THREAD_POOLS.put(threadNamePrefix, threadPool);
        }
        return threadPool;
    }

    public static ExecutorService createThreadPool(String threadNamePrefix) {
        ThreadPoolConfig threadPoolConfig = new ThreadPoolConfig();
        return createThreadPoolIfAbsent(threadPoolConfig, threadNamePrefix);
    }

    public static ExecutorService createThreadPool(String threadNamePrefix, ThreadPoolConfig threadPoolConfig) {
        return createThreadPoolIfAbsent(threadPoolConfig, threadNamePrefix);
    }


    private static ExecutorService createThreadPool(ThreadPoolConfig threadPoolConfig, String threadNamePrefix) {
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix);
        return new ThreadPoolExecutor(threadPoolConfig.getCorePoolSize(), threadPoolConfig.getMaximumPoolSize(),
                threadPoolConfig.getKeepAliveTime(), threadPoolConfig.getUnit(), threadPoolConfig.getWorkQueue(),
                threadFactory);
    }

    public static ThreadFactory createThreadFactory(String threadNamePrefix) {
        if (threadNamePrefix != null) {
            return new ThreadFactoryBuilder()
                    .setNameFormat(threadNamePrefix + "-%d")
                    .setDaemon(false).build();

        }
        return Executors.defaultThreadFactory();
    }

    /**
     * shutDown 线程池
     */
    public static void shutDownThreadPool(String threadNamePrefix) {
        log.info("execute shutdown threadPool: " + threadNamePrefix);
        ExecutorService executorService = THREAD_POOLS.get(threadNamePrefix);
        try {
            executorService.shutdown();
            log.info("shutdown threadPool [{}] [{}]", threadNamePrefix, executorService.isTerminated());
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            log.error("Thread pool shutdown failed, attempting to close again");
            executorService.shutdownNow();
        }
    }


    /**
     * shutDown 所有线程池
     */
    public static void shutDownAllThreadPool() {
        log.info("execute shutdown all threadPool");
        THREAD_POOLS.entrySet().parallelStream().forEach(executorServiceEntry -> {
            shutDownThreadPool(executorServiceEntry.getKey());
        });
    }


}
