package cn.jrymos.spring.custom.injection.test.model;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Getter
public class XxxTask {
    private final XxxService xxxService;
    private final XxxBiz xxxBiz;
    private final ExecutorService e1;
    private final ExecutorService e2;
    private final ExecutorService e3;

    public XxxTask(XxxService xxxService, XxxBiz xxxBiz, XxxConfig.XxxConfigService xxxConfigService,
                   @ThreadPoolExecutorConfig(threadPoolId = "executor1") ExecutorService e1,
                   @ThreadPoolExecutorConfig(corePoolSize = 2, maximumPoolSize = 2, keepAliveTime = 1,
                       timeUnit = TimeUnit.SECONDS, queueSize = 1, threadPoolId = "threadPoolExecutor") ExecutorService e2,
                   @ThreadPoolExecutorConfig(threadPoolId = "hello") ExecutorService e3) {
        this.xxxService = xxxService;
        this.xxxBiz = xxxBiz;
        this.e1 = e1;
        this.e2 = e2;
        this.e3 = e3;

        // 测试验证线程池注入的正确性
        assert xxxService.getExecutor1() == xxxService.getExecutor1();
        assert xxxBiz.getThreadPoolExecutor() == e2;
        assert e3 == xxxConfigService.getThreadPoolExecutor();
        assert e1 != e2;
        assert e1 != e3;
        assert e2 != e3;
    }
}
