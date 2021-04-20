package cn.jrymos.spring.custom.injection.core;


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
import org.springframework.core.ResolvableType;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

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
            if (StringUtils.isEmpty(beanDefinition.getBeanClassName())) {
                continue;
            }
            try {
                Class<?> rawClass = Class.forName(beanDefinition.getBeanClassName());
                if (rawClass.getPackage().getName().startsWith(CustomInjectionCoreConfig.getPackagePrefix())) {
                    for (CustomBeanFactory<Annotation> customBeanFactory : CustomBeanFactoryRegister.getFactories()) {
                        FieldUtils.getFieldsListWithAnnotation(rawClass, customBeanFactory.getAnnotationType())
                            //遍历使用了自定义注解的字段
                            .forEach(field -> registryRootBeanDefinition(registry, field, customBeanFactory));
                    }
                }
            } catch (ClassNotFoundException e) {
                log.error("class not found:", e);
                throw e;
            }
        }
    }

    private void registryRootBeanDefinition(BeanDefinitionRegistry registry, Field field, CustomBeanFactory<Annotation> customBeanFactory) {
        Annotation annotation = field.getAnnotation(customBeanFactory.getAnnotationType());
        String value = customBeanFactory.getAnnotationValue(annotation);
        log.info("registryRootBeanDefinition:{},{},{}", annotation, value, field);
        if (StringUtils.isEmpty(value)) {
            throw new UnsupportedOperationException("not found annotation value " + annotation);
        }
        String beanName = customBeanFactory.getBeanName(annotation);
        if (registry.containsBeanDefinition(beanName)) {
            return;
        }
        BeanDefinition beanDefinition = initBeanDefinition(field, customBeanFactory, annotation);
        registry.registerBeanDefinition(beanName, beanDefinition);
    }

    private BeanDefinition initBeanDefinition(Field field, CustomBeanFactory<Annotation> customBeanFactory, Annotation annotation) {
        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(field.getType());
        beanDefinition.setTargetType(ResolvableType.forField(field));
        beanDefinition.setFactoryBeanName(customBeanFactory.getClass().getName());
        beanDefinition.setFactoryMethodName(customBeanFactory.getFactoryMethodName());
        beanDefinition.setQualifiedElement(field);
        ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
        constructorArgumentValues.addGenericArgumentValue(annotation);
        constructorArgumentValues.addGenericArgumentValue(field.getType());
        beanDefinition.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR);
        beanDefinition.setConstructorArgumentValues(constructorArgumentValues);
        return beanDefinition;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

    }
}
