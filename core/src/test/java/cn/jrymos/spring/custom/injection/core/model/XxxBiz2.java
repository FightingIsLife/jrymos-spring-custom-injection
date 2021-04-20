package cn.jrymos.spring.custom.injection.core.model;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ThreadPoolExecutor;

@Service
@RequiredArgsConstructor
public class XxxBiz2 {
    private final XxxService xxxService;
    @ThreadPoolExecutorConfig(threadPoolId = "threadPoolExecutor")
    private final ThreadPoolExecutor threadPoolExecutor;
}
