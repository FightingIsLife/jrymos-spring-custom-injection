package cn.jrymos.spring.custom.injection.core;


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
        CustomBeanFactory<TestAnnotation1> customBeanFactory = new CustomBeanFactory<TestAnnotation1>() {
            @Override
            public String getAnnotationValue(TestAnnotation1 annotation) {
                return "hello";
            }

            @SneakyThrows
            @Override
            public Method getFactoryMethod() {
                return getClass().getMethod("getObject", TestAnnotation1.class);
            }

            public Object getObject(TestAnnotation1 annotation) {
                return new Object();
            }
        };
        CustomBeanFactoryRegister.register(customBeanFactory);
        Assert.assertTrue(CustomBeanFactoryRegister.getFactories().contains(customBeanFactory));
    }


    @Test(expected = IllegalStateException.class)
    public void registerIllegalStateException() {
        CustomBeanFactory<TestAnnotation> customBeanFactory1 = new CustomBeanFactory<TestAnnotation>() {
            @Override
            public String getAnnotationValue(TestAnnotation annotation) {
                return "hello";
            }

            @SneakyThrows
            @Override
            public Method getFactoryMethod() {
                return getClass().getMethod("getObject", TestAnnotation.class);
            }

            public Object getObject(TestAnnotation annotation) {
                return new Object();
            }
        };
        CustomBeanFactory<TestAnnotation> customBeanFactory2 = new CustomBeanFactory<TestAnnotation>() {
            @Override
            public String getAnnotationValue(TestAnnotation annotation) {
                return "hello";
            }

            @SneakyThrows
            @Override
            public Method getFactoryMethod() {
                return getClass().getMethod("getObject", TestAnnotation.class);
            }

            public Object getObject(TestAnnotation annotation) {
                return new Object();
            }
        };
        CustomBeanFactoryRegister.register(customBeanFactory1);
        CustomBeanFactoryRegister.register(customBeanFactory2);
    }


    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public @interface TestAnnotation1 {
    }


    @Target({ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    @Inherited
    public @interface TestAnnotation {
    }

}