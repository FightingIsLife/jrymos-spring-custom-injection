package cn.jrymos.spring.custom.injection.core;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CustomFactoryMethodParameter {
    private final CustomRootBeanDefinition customRootBeanDefinition;

    public <T> T getAnnotation() {
        return (T) customRootBeanDefinition.getFirstAnnotation();
    }

    public Class getBeanClass() {
        return customRootBeanDefinition.getBeanClass();
    }
}
