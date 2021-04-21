package cn.jrymos.spring.custom.injection.core;

import com.google.common.base.Preconditions;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.core.ResolvableType;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自定义RootBeanDefinition，以便一个支持RootBeanDefinition多个AnnotatedElement
 */
public class CustomRootBeanDefinition extends RootBeanDefinition {
    private final Map<AnnotatedElement, ResolvableType> typeMap;
    private final Map<Annotation, AnnotatedElement> annotatedElementMap;
    private AnnotatedElement old;

    public CustomRootBeanDefinition(AnnotatedElement annotatedElement, ResolvableType resolvableType, Annotation annotation) {
        typeMap = new ConcurrentHashMap<>();
        annotatedElementMap = new ConcurrentHashMap<>();
        setTargetType(resolvableType);
        setQualifiedElement(annotatedElement);
        put(annotatedElement, resolvableType, annotation);
    }

    public CustomRootBeanDefinition(CustomRootBeanDefinition original) {
        super(original);
        typeMap = original.typeMap;
        annotatedElementMap = original.annotatedElementMap;
    }

    public void put(AnnotatedElement annotatedElement, ResolvableType resolvableType, Annotation annotation) {
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
        annotatedElementMap.put(annotation, annotatedElement);
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

    /**
     * never empty
     */
    public List<Annotation> getAnnotations() {
        Preconditions.checkArgument(!annotatedElementMap.isEmpty(), "bad CustomRootBeanDefinition");
        return new ArrayList<>(annotatedElementMap.keySet());
    }

    public Annotation getFirstAnnotation() {
        return getAnnotations().get(0);
    }

    @Override
    public RootBeanDefinition cloneBeanDefinition() {
        return new CustomRootBeanDefinition(this);
    }
}
