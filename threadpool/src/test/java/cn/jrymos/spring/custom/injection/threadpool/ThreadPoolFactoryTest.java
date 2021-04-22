package cn.jrymos.spring.custom.injection.threadpool;


import com.google.common.collect.ImmutableList;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ThreadPoolFactoryTest {

    @Test
    public void test() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
            "cn.jrymos.spring.custom.injection.core", "cn.jrymos.spring.custom.injection.ccc", "cn.jrymos.spring.custom.injection.threadpool");
        XxxService bean = applicationContext.getBean(XxxService.class);
        Assert.assertEquals(applicationContext.getBean("xxxService"), bean);
        Assert.assertEquals(applicationContext.getBean("threadPoolFactory$-pool1"), bean.getThreadPoolExecutor());
        Assert.assertEquals(applicationContext.getBean("threadPoolFactory$-pool2"), bean.getExecutor());
        Assert.assertEquals(applicationContext.getBean("threadPoolFactory$-pool3"), bean.getExecutorService());
        Assert.assertEquals(3, bean.getExecutorServices().size());
        Assert.assertEquals(ImmutableList.of(applicationContext.getBean("threadPoolFactory$-pool1"),
            applicationContext.getBean("threadPoolFactory$-pool2")), bean.getExecutorServices2());
    }
}