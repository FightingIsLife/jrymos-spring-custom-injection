package cn.jrymos.spring.custom.injection.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
public class XxxService {
    @Getter
    @ThreadPoolExecutorConfig(threadPoolId = "executor1")
    private final ExecutorService executor1;
}
