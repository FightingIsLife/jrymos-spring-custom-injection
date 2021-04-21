package cn.jrymos.spring.custom.injection.threadpool;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@RequiredArgsConstructor
public class SimpleThreadFactory implements ThreadFactory {

    private final String name;
    private final AtomicInteger count = new AtomicInteger();

    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, name + count.getAndIncrement());
    }
}
