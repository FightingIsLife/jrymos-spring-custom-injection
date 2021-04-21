package cn.jrymos.spring.custom.injection.memcached;


import cn.jrymos.spring.custom.injection.memcached.model.XxxService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class MemcachedLockFactoryTest {

    @Test
    public void test() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
            "cn.jrymos.spring.custom.injection.core", "cn.jrymos.spring.custom.injection.memcached");
        XxxService bean = applicationContext.getBean(XxxService.class);
        Assert.assertEquals(applicationContext.getBean("xxxService"), bean);
        Assert.assertEquals(applicationContext.getBean("memcachedLockFactory$lock1"), bean.getMemcachedLock11());
        Assert.assertEquals(applicationContext.getBean("memcachedLockFactory$lock1"), bean.getMemcachedLock1());
        Assert.assertEquals(2, bean.getMemcachedLock1().getExpSecs());
        Assert.assertEquals(10, bean.getMemcachedLock2().getExpSecs());
        Assert.assertEquals(applicationContext.getBean("memcachedLockFactory$lock2"), bean.getMemcachedLock2());
    }
}