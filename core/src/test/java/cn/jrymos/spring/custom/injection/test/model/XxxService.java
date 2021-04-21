package cn.jrymos.spring.custom.injection.test.model;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

@Service
//@RequiredArgsConstructor
public class XxxService {
    @Getter
    private final ExecutorService executor1;

    public XxxService(@ThreadPoolExecutorConfig(threadPoolId = "executor1") ExecutorService executor1) {
        this.executor1 = executor1;
    }
}
