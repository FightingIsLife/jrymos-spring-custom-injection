package cn.jrymos.spring.custom.injection.core;


import cn.jrymos.util.ReflectionUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * 自定义BeanDefinition注册到spring
 */
@Slf4j
@Configuration
public class CustomBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    @SneakyThrows
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        String[] beanDefinitionNames = registry.getBeanDefinitionNames();
        if (ObjectUtils.isEmpty(beanDefinitionNames)) {
            return;
        }
        for (String beanDefinitionName : beanDefinitionNames) {
            BeanDefinition beanDefinition = registry.getBeanDefinition(beanDefinitionName);
            try {
                if (StringUtils.isEmpty(beanDefinition.getBeanClassName())) {
                    if (beanDefinition instanceof RootBeanDefinition) {
                        Method method = ((RootBeanDefinition) beanDefinition).getResolvedFactoryMethod();
                        if (method != null && method.getClass().getPackage().getName().startsWith(CustomInjectionCoreConfig.getPackagePrefix())) {
                            for (CustomBeanFactory<Annotation> customBeanFactory : CustomBeanFactoryRegister.getFactories()) {
                                ReflectionUtils.getParametersListWithAnnotation(method, customBeanFactory.getAnnotationType())
                                    //遍历使用了自定义注解的字段
                                    .forEach(parameter -> registryRootBeanDefinition(registry, parameter, customBeanFactory));
                            }

                        }
                    }
                } else {
                    Class<?> rawClass = Class.forName(beanDefinition.getBeanClassName());
                    if (rawClass.getPackage().getName().startsWith(CustomInjectionCoreConfig.getPackagePrefix())) {
                        for (CustomBeanFactory<Annotation> customBeanFactory : CustomBeanFactoryRegister.getFactories()) {
                            FieldUtils.getFieldsListWithAnnotation(rawClass, customBeanFactory.getAnnotationType())
                                //遍历使用了自定义注解的字段
                                .forEach(field -> registryRootBeanDefinition(registry, field, customBeanFactory));
                        }
                    }
                }

            } catch (ClassNotFoundException e) {
                log.error("class not found:", e);
                throw e;
            }
        }
    }

    private void registryRootBeanDefinition(BeanDefinitionRegistry registry, Parameter parameter, CustomBeanFactory<Annotation> customBeanFactory) {
        registryRootBeanDefinition(registry, customBeanFactory,
            parameter.getAnnotation(customBeanFactory.getAnnotationType()), parameter.getType(),
            ResolvableType.forMethodParameter(MethodParameter.forParameter(parameter)),
            parameter);
    }

    private void registryRootBeanDefinition(BeanDefinitionRegistry registry, Field field, CustomBeanFactory<Annotation> customBeanFactory) {
        registryRootBeanDefinition(registry, customBeanFactory,
            field.getAnnotation(customBeanFactory.getAnnotationType()), field.getType(),
            ResolvableType.forField(field),
            field);
    }

    private void registryRootBeanDefinition(BeanDefinitionRegistry registry, CustomBeanFactory<Annotation> customBeanFactory, Annotation annotation,
                                            Class<?> type, ResolvableType targetType, AnnotatedElement annotatedElement) {
        String value = customBeanFactory.getAnnotationValue(annotation);
        log.info("registryRootBeanDefinition:{},{},{}", annotation, value, annotatedElement);
        if (StringUtils.isEmpty(value)) {
            throw new UnsupportedOperationException("not found annotation value " + annotation);
        }
        String beanName = customBeanFactory.getBeanName(annotation);
        if (registry.containsBeanDefinition(beanName)) {
            return;
        }
        BeanDefinition beanDefinition = getBeanDefinition(customBeanFactory, annotation, type, targetType, annotatedElement);
        registry.registerBeanDefinition(beanName, beanDefinition);
    }


    private BeanDefinition getBeanDefinition(CustomBeanFactory<Annotation> customBeanFactory, Annotation annotation,
                                             Class<?> type, ResolvableType targetType, AnnotatedElement annotatedElement) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(type);
        beanDefinition.setTargetType(targetType);
        beanDefinition.setFactoryBeanName(customBeanFactory.getClass().getName());
        beanDefinition.setFactoryMethodName(customBeanFactory.getFactoryMethodName());
        beanDefinition.setQualifiedElement(annotatedElement);
        ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
        constructorArgumentValues.addGenericArgumentValue(annotation);
        constructorArgumentValues.addGenericArgumentValue(type);
        beanDefinition.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR);
        beanDefinition.setConstructorArgumentValues(constructorArgumentValues);
        return beanDefinition;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
