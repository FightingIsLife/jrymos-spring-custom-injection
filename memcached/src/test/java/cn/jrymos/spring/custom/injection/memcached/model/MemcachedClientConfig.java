package cn.jrymos.spring.custom.injection.memcached.model;

import com.whalin.MemCached.MemCachedClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class MemcachedClientConfig {

    @Bean
    public MemCachedClient memCachedClient() {
        return new MemCachedClient();
    }

}
