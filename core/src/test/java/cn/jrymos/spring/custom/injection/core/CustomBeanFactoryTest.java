package cn.jrymos.spring.custom.injection.core;

import cn.jrymos.spring.custom.injection.core.model.XxxTask;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class CustomBeanFactoryTest {

    @Test
    public void test() {
        CustomInjectionCoreConfig.getConfig().setPackagePrefix("cn.jrymos.spring.custom.injection.core.model");
        CustomInjectionCoreConfig.getConfig().setAutoScanCustomBeanFactoryPackage("cn.jrymos.spring.custom.injection.core.model");
        CustomInjectionCoreConfig.getConfig().setEnhanceConstructParameterByFieldAnnotation(true);
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext("cn.jrymos.spring.custom.injection.core");
        applicationContext.scan("cn.jrymos.spring.custom.injection.core.model");
        XxxTask bean = applicationContext.getBean(XxxTask.class);
        Assert.assertEquals(applicationContext.getBean("xxxService"), bean.getXxxService());
        Assert.assertEquals(applicationContext.getBean("xxxBiz"), bean.getXxxBiz());
        Assert.assertEquals(applicationContext.getBean("threadPoolExecutorBeanFactory$executor1"), bean.getE1());
        Assert.assertEquals(applicationContext.getBean("threadPoolExecutorBeanFactory$threadPoolExecutor"), bean.getE2());
        Assert.assertEquals(applicationContext.getBean("threadPoolExecutorBeanFactory$hello"), bean.getE3());
    }

}
