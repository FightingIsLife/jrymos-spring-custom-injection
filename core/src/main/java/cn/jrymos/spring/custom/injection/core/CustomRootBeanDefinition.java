package cn.jrymos.spring.custom.injection.core;

import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.ResolvableType;

import java.lang.reflect.AnnotatedElement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义RootBeanDefinition，以便一个支持RootBeanDefinition多个AnnotatedElement
 */
public class CustomRootBeanDefinition extends RootBeanDefinition {
    private final Map<AnnotatedElement, ResolvableType> typeMap;
    private AnnotatedElement old;

    public CustomRootBeanDefinition() {
        typeMap = new ConcurrentHashMap<>();
    }

    public CustomRootBeanDefinition(CustomRootBeanDefinition original) {
        super(original);
        typeMap = original.typeMap;
    }

    public void put(AnnotatedElement annotatedElement, ResolvableType resolvableType) {
        // 同一个RootBeanDefinition，支持的多个ResolvableType必须是相同的类型或者两者是继承关系
        if (getTargetType() != null && !resolvableType.isAssignableFrom(getTargetType()) && !getTargetType().isAssignableFrom(resolvableType.getRawClass())) {
            throw new IllegalArgumentException("un support use not assignable type " + resolvableType + "," + getTargetType());
        }
        // 设置为子类这样可以同时支持父类
        if (getTargetType() != null && getTargetType().isAssignableFrom(resolvableType.getRawClass())) {
            setTargetType(resolvableType);
            setQualifiedElement(annotatedElement);
        }
        typeMap.put(annotatedElement, resolvableType);
    }

    public void safeChange(DependencyDescriptor descriptor) {
        AnnotatedElement annotatedElement = descriptor.getField();
        if (annotatedElement == null && descriptor.getMethodParameter() != null) {
            annotatedElement = descriptor.getMethodParameter().getParameter();
        }
        if (typeMap.containsKey(annotatedElement)) {
            old = getQualifiedElement();
            setQualifiedElement(annotatedElement);
            setTargetType(typeMap.get(annotatedElement));
        }
    }

    public void safeChangeToOld() {
        if (old != null) {
            setQualifiedElement(old);
            setTargetType(typeMap.get(old));
        }
    }

    @Override
    public RootBeanDefinition cloneBeanDefinition() {
        return new CustomRootBeanDefinition(this);
    }
}
