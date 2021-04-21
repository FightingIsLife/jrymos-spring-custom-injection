package cn.jrymos.spring.custom.injection.threadpool;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@RequiredArgsConstructor
@Getter
public class XxxService {

    @ThreadPoolConfig(id = "pool1")
    private final ThreadPoolExecutor threadPoolExecutor;
    @ThreadPoolConfig(id = "pool2", coreS = 2, qSize = 999)
    private final Executor executor;
    @ThreadPoolConfig(id = "pool3", maxS = 10, qSize = 0, reject = ThreadPoolExecutor.DiscardPolicy.class)
    private final ExecutorService executorService;

}
