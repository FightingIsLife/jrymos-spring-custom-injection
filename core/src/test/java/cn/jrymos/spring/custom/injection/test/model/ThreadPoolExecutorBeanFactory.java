package cn.jrymos.spring.custom.injection.test.model;

import cn.jrymos.spring.custom.injection.core.CustomBeanFactory;
import cn.jrymos.spring.custom.injection.core.CustomFactoryMethodParameter;
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
public class ThreadPoolExecutorBeanFactory extends CustomBeanFactory<ThreadPoolExecutorConfig, ThreadPoolExecutor> {

    @SneakyThrows
    public ThreadPoolExecutor factoryMethod(CustomFactoryMethodParameter<ThreadPoolExecutorConfig> customFactoryMethodParameter) {
        ThreadPoolExecutorConfig config = customFactoryMethodParameter.getAnnotation();
        return new AutoShutdownThreadPoolExecutor(config.corePoolSize(), Math.max(config.corePoolSize(), config.maximumPoolSize()), config.keepAliveTime(),
            config.timeUnit(), new LinkedBlockingQueue<>(config.queueSize()), new SimpleThreadFactory(config.threadPoolId()), config.reject().newInstance());
    }

    @SneakyThrows
    public Method getFactoryMethod() {
        return getClass().getMethod("factoryMethod", CustomFactoryMethodParameter.class);
    }

    @Override
    public Class<ThreadPoolExecutorConfig> getAnnotationType() {
        return ThreadPoolExecutorConfig.class;
    }

    @Override
    public String getBeanValue(ThreadPoolExecutorConfig annotation) {
        return annotation.threadPoolId();
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
}
