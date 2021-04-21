package cn.jrymos.spring.custom.injection.test.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class XxxBiz2 {
    private final XxxService xxxService;
    @ThreadPoolExecutorConfig(corePoolSize = 2, maximumPoolSize = 2, keepAliveTime = 1,
        timeUnit = TimeUnit.SECONDS, queueSize = 1, threadPoolId = "threadPoolExecutor")
    private final ThreadPoolExecutor threadPoolExecutor;
}
