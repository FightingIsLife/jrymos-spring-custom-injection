package cn.jrymos.spring.custom.injection.threadpool;

import cn.jrymos.spring.custom.injection.ccc.CccCollection;
import cn.jrymos.spring.custom.injection.ccc.CccMap;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
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

    private final List<ExecutorService> executorServices;

    @CccCollection(value = {"pool1", "pool2"}, prefixClass = ThreadPoolFactory.class)
    private final List<ExecutorService> executorServices2;

    @CccMap(value = {"pool1", "pool3"}, prefixClass = ThreadPoolFactory.class)
    private final Map<String, ExecutorService> executorServices3;
}
