package cn.jrymos.spring.custom.injection.core;


import cn.jrymos.spring.custom.injection.core.CustomBeanFactory;
import cn.jrymos.spring.custom.injection.core.CustomBeanFactoryRegister;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.Test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

public class CustomBeanFactoryRegisterTest {

    @Test(expected = NullPointerException.class)
    public void registerNpe() {
        CustomBeanFactoryRegister.register(null);
    }

    @Test
    public void register() {
        CustomBeanFactory<TestAnnotation1, Object> customBeanFactory = new CustomBeanFactoryT1() {};
        CustomBeanFactoryRegister.register(customBeanFactory);
        Assert.assertTrue(CustomBeanFactoryRegister.getFactories().contains(customBeanFactory));
    }


    @Test(expected = IllegalStateException.class)
    public void registerIllegalStateException() {
        CustomBeanFactory<TestAnnotation, Object> customBeanFactory1 = new CustomBeanFactoryT() {};
        CustomBeanFactory<TestAnnotation, Object> customBeanFactory2 = new CustomBeanFactoryT() {};
        CustomBeanFactoryRegister.register(customBeanFactory1);
        CustomBeanFactoryRegister.register(customBeanFactory2);
    }


    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public @interface TestAnnotation1 {
        String value();
    }


    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public @interface TestAnnotation {
        String value();
    }


    public class CustomBeanFactoryT1 extends CustomBeanFactory<TestAnnotation1, Object> {
        @Override
        public Class<TestAnnotation1> getAnnotationType() {
            return TestAnnotation1.class;
        }

        @SneakyThrows
        @Override
        public Method getFactoryMethod() {
            return getClass().getMethod("getAnnotationType");
        }
    }


    public class CustomBeanFactoryT extends CustomBeanFactory<TestAnnotation, Object> {
        @Override
        public Class<TestAnnotation> getAnnotationType() {
            return TestAnnotation.class;
        }

        @SneakyThrows
        @Override
        public Method getFactoryMethod() {
            return getClass().getMethod("getAnnotationType");
        }
    }

}