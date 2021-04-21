package cn.jrymos.spring.custom.injection.test.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class XxxBiz {
    private final XxxService xxxService;
    @Getter
    @ThreadPoolExecutorConfig(corePoolSize = 2, maximumPoolSize = 2, keepAliveTime = 1,
        timeUnit = TimeUnit.SECONDS, queueSize = 1, threadPoolId = "threadPoolExecutor")
    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;
}
