package cn.jrymos.spring.custom.injection.core;

import com.google.common.collect.ImmutableSet;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.core.MethodParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 处理 自定义spring 类似Qualifier注解功能的匹配器，如"RedisKey"注解
 * 增强了Qualifier注解的使用，支持
 */
@Slf4j
public class CustomContextAnnotationAutowireCandidateResolver extends ContextAnnotationAutowireCandidateResolver {

    private static final Set<Class<? extends Annotation>> CUSTOM_QUALIFIER_ANNOTATIONS = CustomBeanFactoryRegister.getFactories()
        .stream().map(CustomBeanFactory::getAnnotationType).collect(Collectors.toSet());
    private static final Set<Class<? extends Annotation>> QUALIFIER_ANNOTATIONS = ImmutableSet.<Class<? extends Annotation>>builder()
        .addAll(CUSTOM_QUALIFIER_ANNOTATIONS)
        .add(Qualifier.class)
        .build();

    @Override
    public boolean isAutowireCandidate(BeanDefinitionHolder bdHolder, DependencyDescriptor descriptor) {
        if (bdHolder.getBeanDefinition() instanceof CustomRootBeanDefinition) {
            // 切换annotationElement, CustomRootBeanDefinition支持设置多个annotationElement
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
        for (CustomBeanFactory<Annotation> customBeanFactory : CustomBeanFactoryRegister.getFactories()) {
            customBeanFactory.checkDependencyDescriptor(descriptor, annotations);
        }
    }

    private List<Annotation> getQualifierAnnotations(DependencyDescriptor descriptor) {
        if (ObjectUtils.isNotEmpty(descriptor.getAnnotations())) {
            return Arrays.stream(descriptor.getAnnotations())
                .filter(annotation -> QUALIFIER_ANNOTATIONS.contains(annotation.annotationType()))
                .collect(Collectors.toList());
        }
        /*
         * 增强Qualifier类型的注解使用范围
         * 获取methodParameter对应赋值字段上的注解，匹配赋值字段规则：字段名称和类型都相同的（非static）字段
         * 原因：支持lombok的@RequiredArgsConstructor、@AllArgsConstructor的使用
         */
        MethodParameter methodParameter = descriptor.getMethodParameter();
        if (methodParameter != null && CustomInjectionCoreConfig.getConfig().isEnhanceConstructParameterByFieldAnnotation()) {
            Field field = FieldUtils.getField(methodParameter.getExecutable().getDeclaringClass(), methodParameter.getParameterName(), true);
            return Optional.ofNullable(field)
                .filter(f -> f.getType().isAssignableFrom(methodParameter.getParameterType()))
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .map(this::getQualifierAnnotations)
                .orElse(Collections.emptyList());
        }
        return Collections.emptyList();
    }

    private List<Annotation> getQualifierAnnotations(Field field) {
        Annotation[] annotations = field.getAnnotations();
        if (annotations == null || annotations.length == 0) {
            return Collections.emptyList();
        }
        return Arrays.stream(annotations)
            .filter(annotation -> QUALIFIER_ANNOTATIONS.contains(annotation.annotationType()))
            .collect(Collectors.toList());
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
