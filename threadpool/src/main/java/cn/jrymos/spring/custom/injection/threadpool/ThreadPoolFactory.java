package cn.jrymos.spring.custom.injection.threadpool;

import cn.jrymos.spring.custom.injection.core.CustomBeanFactory;
import cn.jrymos.spring.custom.injection.core.CustomFactoryMethodParameter;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class ThreadPoolFactory extends CustomBeanFactory<ThreadPoolConfig, ThreadPoolExecutor> {

    @SneakyThrows
    @Override
    public ThreadPoolExecutor factoryMethod(CustomFactoryMethodParameter<ThreadPoolConfig> customFactoryMethodParameter) {
        ThreadPoolConfig config = customFactoryMethodParameter.getAnnotation();
        BlockingQueue<Runnable> queue = getQueue(config.qSize());
        return new SimpleThreadPoolExecutor(config.id(), config.coreS(), Math.max(config.maxS(), config.coreS()), config.seconds(),
            TimeUnit.SECONDS, queue, new SimpleThreadFactory(config.id()), config.reject().newInstance());
    }

    private BlockingQueue<Runnable> getQueue(int qSize) {
        if (qSize == 0) {
            return new SynchronousQueue<>();
        } else if (qSize < 1000) {
            return new ArrayBlockingQueue<>(qSize);
        } else {
            return new LinkedBlockingQueue<>(qSize);
        }
    }

    @Override
    public void checkAndUpdateCustomFactoryMethodParameter(CustomFactoryMethodParameter<ThreadPoolConfig> customFactoryMethodParameter) {
        // 一个线程池只能注入到一个bean
        if (customFactoryMethodParameter.getAnnotations().size() != 1) {
            throw new IllegalArgumentException("not support " + customFactoryMethodParameter.getAnnotations());
        }
    }

    @Override
    public Class<ThreadPoolConfig> getAnnotationType() {
        return ThreadPoolConfig.class;
    }


    @Override
    public String getBeanValue(ThreadPoolConfig annotation) {
        return annotation.id();
    }
}
