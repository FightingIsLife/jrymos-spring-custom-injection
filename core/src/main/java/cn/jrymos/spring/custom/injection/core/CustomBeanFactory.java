package cn.jrymos.spring.custom.injection.core;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 自定义注解的实例工厂
 * 每次新增自定义注解，只需要实现CustomBeanFactory方法
 * @param <T> 注解的元注解至少应该有@Target({ElementType.FIELD})、@Retention(RetentionPolicy.RUNTIME)
 */
public interface CustomBeanFactory<T extends Annotation> {


    /**
     * 获取支持的注解的value
     */
    String getAnnotationValue(T annotation);

    /**
     * 获取工厂方法，工厂方法需要满足的条件：
     * @see this#getAnnotationType() 第一个参数是支持注解
     * @see this#getBeanClass() 返回结果是要生产的bean实例
     * @see ThreadPoolExecutorBeanFactory#getThreadPoolExecutor(ThreadPoolExecutorConfig config)  例子
     */
    Method getFactoryMethod();

    /**
     * 自定义注解class
     */
    default Class<T> getAnnotationType() {
        return (Class<T>) getFactoryMethod().getParameterTypes()[0];
    }

    /**
     * 工厂生成的bean class
     */
    default Class<?> getBeanClass() {
        return getFactoryMethod().getReturnType();
    }

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