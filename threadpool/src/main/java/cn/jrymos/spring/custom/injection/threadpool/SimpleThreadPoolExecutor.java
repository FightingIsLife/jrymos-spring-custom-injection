package cn.jrymos.spring.custom.injection.threadpool;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Getter
@Slf4j
public class SimpleThreadPoolExecutor extends ThreadPoolExecutor implements DisposableBean {
    private final String id;

    public SimpleThreadPoolExecutor(String id, int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit, BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory, RejectedExecutionHandler handler) {
        super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
        this.id = id;
    }

    @Override
    public void destroy() throws Exception {
        shutdown();
        awaitTermination(5, TimeUnit.SECONDS);
        log.info("{} thread pool destroy", id);
    }
}
