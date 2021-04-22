package cn.jrymos.spring.custom.injection.core;

import cn.jrymos.spring.custom.injection.test.model.XxxTask;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class CustomBeanFactoryTest {

    @Test
    public void test() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
            "cn.jrymos.spring.custom.injection.core", "cn.jrymos.spring.custom.injection.test.model");
        XxxTask bean = applicationContext.getBean(XxxTask.class);
        Assert.assertEquals(applicationContext.getBean("xxxService"), bean.getXxxService());
        Assert.assertEquals(applicationContext.getBean("xxxBiz"), bean.getXxxBiz());
        Assert.assertEquals(applicationContext.getBean("threadPoolExecutorBeanFactory$-executor1"), bean.getE1());
        Assert.assertEquals(applicationContext.getBean("threadPoolExecutorBeanFactory$-threadPoolExecutor"), bean.getE2());
        Assert.assertEquals(applicationContext.getBean("threadPoolExecutorBeanFactory$-hello"), bean.getE3());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testError() {
        new AnnotationConfigApplicationContext(
            "cn.jrymos.spring.custom.injection.core",
            "cn.jrymos.spring.custom.injection.test.model",
            "cn.jrymos.spring.custom.injection.test.bad.model");
    }
}
