package cn.jrymos.spring.custom.injection.memcached;

import com.whalin.MemCached.MemCachedClient;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractOwnableSynchronizer;

/**
 * 一个简易的memcachedLock实现
 */
public class MemcachedLock extends AbstractOwnableSynchronizer {
    private static final int LOCK_SIGNAL = 1;
    private final MemCachedClient memCachedClient;
    private final String identify;
    private final int expSecs; //单机情况由于exclusiveOwnerThread的存在而无效

    public MemcachedLock(MemCachedClient memCachedClient, String identify, int expSecs) {
        this.memCachedClient = memCachedClient;
        this.identify = identify;
        this.expSecs = expSecs;
    }

    public boolean tryLock() {
        Thread currentThread = Thread.currentThread();
        Thread exclusiveOwnerThread = getExclusiveOwnerThread();
        // 支持重入
        if (exclusiveOwnerThread == currentThread) {
            return true;
        }
        // 锁已经被其他线程占据
        if (exclusiveOwnerThread != null) {
            return false;
        }
        if (memCachedClient.add(identify, LOCK_SIGNAL, new Date(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(expSecs)))) {
            setExclusiveOwnerThread(currentThread);
            return true;
        }
        // 竞争失败
        return false;
    }

    public boolean release() {
        // 防止其他线程不良的release
        if (getExclusiveOwnerThread() != Thread.currentThread()) {
            return false;
        }
        setExclusiveOwnerThread(null);
        memCachedClient.delete(identify);
        return true;
    }
}
