package cn.jrymos.spring.custom.injection.redisson.model;

import ai.grakn.redismock.RedisServer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class RedissonClientConfig {

    private static volatile RedisServer redisServer;

    @Bean
    public RedissonClient redissonSingleClient() {
        initRedisServer();
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + redisServer.getHost() + ":" + redisServer.getBindPort());
        config.useSingleServer().setConnectionMinimumIdleSize(4);
        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);
    }

    private static synchronized void initRedisServer() {
        if (redisServer == null) {
            for (int i = 0; i < 5; i++) {
                int port = RandomUtils.nextInt(10000, 65535);
                try {
                    redisServer = RedisServer.newRedisServer(port);
                    redisServer.start();
                    break;
                } catch (IOException e) {
                    log.error("create redisServer failed", e);
                }
            }
        }
    }
}
