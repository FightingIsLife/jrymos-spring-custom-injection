package cn.jrymos.spring.custom.injection.threadpool;


import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ThreadPoolFactoryTest {

    @Test
    public void test() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
            "cn.jrymos.spring.custom.injection.core", "cn.jrymos.spring.custom.injection.threadpool");
        XxxService bean = applicationContext.getBean(XxxService.class);
        Assert.assertEquals(applicationContext.getBean("xxxService"), bean);
        Assert.assertEquals(applicationContext.getBean("threadPoolFactory$pool1"), bean.getThreadPoolExecutor());
        Assert.assertEquals(applicationContext.getBean("threadPoolFactory$pool2"), bean.getExecutor());
        Assert.assertEquals(applicationContext.getBean("threadPoolFactory$pool3"), bean.getExecutorService());
    }
}