package cn.jrymos.spring.custom.injection.memcached.model;

import cn.jrymos.spring.custom.injection.core.CustomFactoryMethodParameter;
import cn.jrymos.spring.custom.injection.memcached.MemcachedLock;
import cn.jrymos.spring.custom.injection.memcached.MemcachedLockConfig;
import com.whalin.MemCached.MemCachedClient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
public class XxxService {

    @MemcachedLockConfig(identify = "lock1", expSecs = 2)
    private final MemcachedLock memcachedLock11;

    /**
     * 将会使用memcachedLock11的expSecs
     * @see cn.jrymos.spring.custom.injection.memcached.MemcachedLockFactory#checkAndUpdateCustomFactoryMethodParameter(CustomFactoryMethodParameter) 重写了检查
     */
    @MemcachedLockConfig(identify = "lock1")
    private final MemcachedLock memcachedLock1;

    /**
     * @see cn.jrymos.spring.custom.injection.memcached.MemcachedLockFactory#getMemcachedLock(CustomFactoryMethodParameter, MemCachedClient) 使用默认的10s
     */
    @MemcachedLockConfig(identify = "lock2")
    private final MemcachedLock memcachedLock2;

}
