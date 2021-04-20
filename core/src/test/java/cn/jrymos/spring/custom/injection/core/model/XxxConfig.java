package cn.jrymos.spring.custom.injection.core.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ThreadPoolExecutor;

@Configuration
public class XxxConfig {

    @Bean
    public XxxConfigService xxxConfigService(@ThreadPoolExecutorConfig(threadPoolId = "hello") ThreadPoolExecutor threadPoolExecutor) {
        return new XxxConfigService(threadPoolExecutor);
    }


    @RequiredArgsConstructor
    @Getter
    public static class XxxConfigService {
        private final ThreadPoolExecutor threadPoolExecutor;
    }
}
