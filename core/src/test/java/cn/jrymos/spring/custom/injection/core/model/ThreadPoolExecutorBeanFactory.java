package cn.jrymos.spring.custom.injection.core.model;

import cn.jrymos.spring.custom.injection.core.CustomBeanFactory;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 支持由spring管理线程池的线程池工厂
 */
@Slf4j
@Configuration
public class ThreadPoolExecutorBeanFactory implements CustomBeanFactory<ThreadPoolExecutorConfig> {

    /**
     * <pre>
     * //如果重写了
     * public boolean isNeedTypeArgs() {
     *    return true;
     * }
     * //将会多一个clazz参数过来，这个clazz可能是ThreadPoolExecutor.class、也可能是ExecutorService.class，取决于业务代码如何写
     * public ThreadPoolExecutor getThreadPoolExecutor(ThreadPoolExecutorConfig config, Class clazz)
     * throws IllegalAccessException, InstantiationException {
     *      ...
     * }
     * </pre>
     */
    public ThreadPoolExecutor getThreadPoolExecutor(ThreadPoolExecutorConfig config) throws IllegalAccessException, InstantiationException {
        return new AutoShutdownThreadPoolExecutor(config.corePoolSize(), Math.max(config.corePoolSize(), config.maximumPoolSize()), config.keepAliveTime(),
            config.timeUnit(), new LinkedBlockingQueue<>(config.queueSize()), new SimpleThreadFactory(config.threadPoolId()), config.reject().newInstance());
    }

    @Override
    public String getAnnotationValue(ThreadPoolExecutorConfig annotation) {
        return annotation.threadPoolId();
    }

    @SneakyThrows
    @Override
    public Method getFactoryMethod() {
        return getClass().getMethod("getThreadPoolExecutor", ThreadPoolExecutorConfig.class);
    }

    @RequiredArgsConstructor
    public static class SimpleThreadFactory implements ThreadFactory {
        private final AtomicInteger atomicInteger = new AtomicInteger();
        private final String threadFactoryName;

        @Override
        public Thread newThread(Runnable r) {
            return new Thread(r, threadFactoryName + "-" + atomicInteger.getAndIncrement());
        }
    }

    public static class AutoShutdownThreadPoolExecutor extends ThreadPoolExecutor implements DisposableBean {

        public AutoShutdownThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                              BlockingQueue<Runnable> workQueue, SimpleThreadFactory threadFactory, RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        }

        // 支持spring关闭线程池
        @Override
        public void destroy() throws InterruptedException {
            log.info("{} threadPool start destroy", ((SimpleThreadFactory) getThreadFactory()).threadFactoryName);
            super.shutdown();
            awaitTermination(1, TimeUnit.SECONDS);
            log.info("{} threadPool end destroy", ((SimpleThreadFactory) getThreadFactory()).threadFactoryName);
        }
    }

    @Override
    public boolean openCheckDependencyDescriptor() {
        return true;
    }
}
