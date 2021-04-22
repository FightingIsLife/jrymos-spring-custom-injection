package cn.jrymos.spring.custom.injection.core;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.ResolvableType;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
public class CustomFactoryMethodParameter<T extends Annotation> {
    private final CustomRootBeanDefinition customRootBeanDefinition;
    private final ListableBeanFactory beanFactory;
    /**
     * 一般使用first annotation的属性来创建bean
     */
    @Setter
    private T firstAnnotation;

    public T getAnnotation() {
        return firstAnnotation == null ? (T) customRootBeanDefinition.getFirstAnnotation() : (T) firstAnnotation;
    }

    public Class getBeanClass() {
        return customRootBeanDefinition.getBeanClass();
    }

    public List<T> getAnnotations() {
        return (List<T>) customRootBeanDefinition.getAnnotations();
    }


    public Object getBean(String name) {
        return beanFactory.getBean(name);
    }

    public Object getBean(Class type) {
        return beanFactory.getBean(type);
    }

    public Object getBean(String name, Class type) {
        return beanFactory.getBean(name, type);
    }

    public List<Class<?>> getBeanClasses() {
        return customRootBeanDefinition.getBeanClasses();
    }

    public ResolvableType getResolvableType() {
        return customRootBeanDefinition.getResolvableType();
    }

    public Map<String, Object> getBeans(Class type) {
        return beanFactory.getBeansOfType(type);
    }
}
