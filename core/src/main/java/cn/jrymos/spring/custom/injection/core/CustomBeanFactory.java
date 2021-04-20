package cn.jrymos.spring.custom.injection.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 自定义注解的实例工厂
 * 每次新增自定义注解，只需要实现CustomBeanFactory方法
 */
public interface CustomBeanFactory<T extends Annotation> {

    /**
     * 自定义注解class
     */
    Class<T> getAnnotationType();

    /**
     * 工厂生成的bean class
     */
    Class<?> getBeanClass();

    /**
     * 获取支持的注解的value
     */
    String getAnnotationValue(T annotation);

    /**
     * 获取工厂方法
     */
    Method getFactoryMethod();

    /**
     * spring 管理的bean的name
     */
    default String getBeanName(T annotation) {
        return this.getClass() + "$" + getAnnotationValue(annotation);
    }

    /**
     * 生成bean的工厂方法名称
     */
    default String getFactoryMethodName() {
        return getFactoryMethod().getName();
    }

}