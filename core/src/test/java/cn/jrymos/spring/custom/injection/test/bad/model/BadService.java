package cn.jrymos.spring.custom.injection.test.bad.model;

import cn.jrymos.spring.custom.injection.test.model.ThreadPoolExecutorConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

@Service
@RequiredArgsConstructor
public class BadService {

    // same threadPoolId but two corePoolSize config

    @ThreadPoolExecutorConfig(corePoolSize = 2, threadPoolId = "tpl")
    private final ThreadPoolExecutor threadPoolExecutor1;
    @ThreadPoolExecutorConfig(corePoolSize = 1, threadPoolId = "tpl")
    private final ThreadPoolExecutor threadPoolExecutor2;
}
