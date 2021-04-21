package cn.jrymos.spring.custom.injection.core;

import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.lang.annotation.Annotation;
import java.util.List;

@RequiredArgsConstructor
public class CustomFactoryMethodParameter<T extends Annotation> {
    private final CustomRootBeanDefinition customRootBeanDefinition;
    /**
     * 一般使用first annotation的属性来创建bean
     */
    @Setter
    private T firstAnnotation;

    public T getFirstAnnotation() {
        return firstAnnotation == null ? (T) customRootBeanDefinition.getFirstAnnotation() : (T) firstAnnotation;
    }

    public Class getBeanClass() {
        return customRootBeanDefinition.getBeanClass();
    }

    public List<T> getAnnotations() {
        return (List<T>) customRootBeanDefinition.getAnnotations();
    }
}
