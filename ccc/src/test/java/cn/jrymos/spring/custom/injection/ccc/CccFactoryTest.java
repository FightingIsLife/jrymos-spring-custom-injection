package cn.jrymos.spring.custom.injection.ccc;


import cn.jrymos.spring.custom.injection.ccc.model.Bird;
import cn.jrymos.spring.custom.injection.ccc.model.Cat;
import cn.jrymos.spring.custom.injection.ccc.model.Duck;
import cn.jrymos.spring.custom.injection.ccc.model.XxxService;
import com.google.common.collect.ImmutableSet;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CccFactoryTest {

    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(
        "cn.jrymos.spring.custom.injection.core", "cn.jrymos.spring.custom.injection.ccc");

    @Test
    public void test() {
        XxxService xxxService = (XxxService) applicationContext.getBean("xxxService");
        Assert.assertEquals(3, xxxService.getAnimalMap().size());
        Assert.assertEquals(3, xxxService.getAnimalSet().size());
        Assert.assertEquals(ImmutableSet.of(Bird.class, Duck.class, Cat.class), xxxService.getMap1().keySet());
        Assert.assertEquals(ImmutableSet.of("duck", "cat"), xxxService.getMap2().keySet());
        Assert.assertEquals(ImmutableSet.of(Bird.class, Duck.class), xxxService.getMap3().keySet());
        Assert.assertEquals(ImmutableSet.of("water", "cat"), xxxService.getMap4().keySet());
        Assert.assertEquals(ImmutableSet.of("water", "cat"), xxxService.getMap4().keySet());

        Assert.assertEquals(ImmutableSet.copyOf(getBeans("water", "cat", "bird")), xxxService.getSet());
        Assert.assertEquals(ImmutableSet.copyOf(getBeans("bird", "duck", "cat")), xxxService.getTreeSet());
        Assert.assertEquals(ImmutableSet.of(), ImmutableSet.copyOf(xxxService.getEmpty()));
        Assert.assertEquals(getBeans("water"), xxxService.getCollection());
        Assert.assertEquals(getBeans("water"), xxxService.getList());
    }

    private List<Object> getBeans(String...beanNames) {
        return Arrays.stream(beanNames)
            .map(applicationContext::getBean)
            .collect(Collectors.toList());
    }
}