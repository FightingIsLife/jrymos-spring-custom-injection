package cn.jrymos.spring.custom.injection.redisson;

import cn.jrymos.spring.custom.injection.redisson.model.XxxService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class RedissonObjectBeanFactoryTest {

    @Test
    public void test() {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
            "cn.jrymos.spring.custom.injection.core", "cn.jrymos.spring.custom.injection.redisson");
        XxxService bean = applicationContext.getBean(XxxService.class);
        Assert.assertEquals(applicationContext.getBean("xxxService"), bean);
        Assert.assertEquals(applicationContext.getBean("redissonObjectBeanFactory$hello"), bean.getLongRBucket());
        Assert.assertEquals(applicationContext.getBean("redissonObjectBeanFactory$hello"), bean.getRBucket());
        Assert.assertEquals(applicationContext.getBean("redissonObjectBeanFactory$testList"), bean.getLongRList());
        Assert.assertEquals(applicationContext.getBean("redissonObjectBeanFactory$map"), bean.getStringLongRMap());
    }
}