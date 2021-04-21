package cn.jrymos.spring.custom.injection.core;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 处理 自定义spring 类似Qualifier注解功能的匹配器，如"RedisKey"注解
 * 增强了Qualifier注解的使用，支持
 */
@Slf4j
public class CustomContextAnnotationAutowireCandidateResolver extends ContextAnnotationAutowireCandidateResolver {

    private final Set<Class<? extends Annotation>> CUSTOM_QUALIFIER_ANNOTATIONS = CustomBeanFactoryRegister.getFactories()
        .stream().map(CustomBeanFactory::getAnnotationType).map(t -> (Class<? extends Annotation>) t).collect(Collectors.toSet());
    private final Set<Class<? extends Annotation>> QUALIFIER_ANNOTATIONS = ImmutableSet.<Class<? extends Annotation>>builder()
        .addAll(CUSTOM_QUALIFIER_ANNOTATIONS)
        .add(Qualifier.class)
        .build();

    @Override
    public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        if (bdHolder.getBeanDefinition() instanceof CustomRootBeanDefinition) {
            // 切换annotationElement, CustomRootBeanDefinition支持设置多个annotationElement
            descriptor = new CustomDependencyDescriptor(descriptor);
            ((CustomRootBeanDefinition) bdHolder.getBeanDefinition()).safeChange(descriptor);
        }
        // 父类匹配会对类型、Qualifier注解进行匹配
        boolean match = super.isAutowireCandidate(bdHolder, descriptor);
        List<Annotation> qualifierAnnotations = getQualifierAnnotations(descriptor);
        // 有必要校验一些依赖必须要有自定义注解
        checkDependencyDescriptor(descriptor, qualifierAnnotations);
        boolean result = match && isMatchQualifierAnnotation(qualifierAnnotations, bdHolder);
        if (bdHolder.getBeanDefinition() instanceof CustomRootBeanDefinition) {
            // 还原成旧的
            ((CustomRootBeanDefinition) bdHolder.getBeanDefinition()).safeChangeToOld();
        }
        return result;
    }

    private void checkDependencyDescriptor(DependencyDescriptor descriptor, List<Annotation> annotations) {
        for (CustomBeanFactory customBeanFactory : CustomBeanFactoryRegister.getFactories()) {
            customBeanFactory.checkDependencyDescriptor(descriptor, annotations);
        }
    }

    private List<Annotation> getQualifierAnnotations(DependencyDescriptor descriptor) {
        if (ObjectUtils.isNotEmpty(descriptor.getAnnotations())) {
            return Arrays.stream(descriptor.getAnnotations())
                .filter(annotation -> QUALIFIER_ANNOTATIONS.contains(annotation.annotationType()))
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }


    /**
     * 根据QualifierAnnotation注解匹配
     */
    private boolean isMatchQualifierAnnotation(List<Annotation> qualifierAnnotations, BeanDefinitionHolder beanName) {
        boolean superMatch = super.checkQualifiers(beanName, qualifierAnnotations.toArray(new Annotation[]{}));
        boolean match = qualifierAnnotations.stream()
            .filter(annotation -> CUSTOM_QUALIFIER_ANNOTATIONS.contains(annotation.annotationType()))
            .map(annotation -> CustomBeanFactoryRegister.getUnique(annotation).getBeanName(annotation))
            .allMatch(name -> beanName.getBeanName().equals(name));
        return superMatch && match;
    }

    public Set<Class<? extends Annotation>> getCustomQualifierTypes() {
        return CUSTOM_QUALIFIER_ANNOTATIONS;
    }
}
