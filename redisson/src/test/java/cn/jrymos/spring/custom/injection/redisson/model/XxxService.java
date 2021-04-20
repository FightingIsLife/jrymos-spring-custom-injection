package cn.jrymos.spring.custom.injection.redisson.model;

import cn.jrymos.spring.custom.injection.redisson.RedissonKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RList;
import org.redisson.api.RMap;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
public class XxxService {

    @RedissonKey(redisKey = "hello")
    private final RBucket<Long> longRBucket;

    @RedissonKey(redisKey = "hello")
    private final RBucket<Long> rBucket;

    @RedissonKey(redisKey = "testList")
    private final RList<Long> longRList;

    @RedissonKey(redisKey = "map")
    private final RMap<String, Long> stringLongRMap;
}
