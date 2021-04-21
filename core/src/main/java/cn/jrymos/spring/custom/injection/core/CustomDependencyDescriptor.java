package cn.jrymos.spring.custom.injection.core;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.core.MethodParameter;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;

public class CustomDependencyDescriptor extends DependencyDescriptor {
    public CustomDependencyDescriptor(DependencyDescriptor original) {
        super(original);
    }

    @Override
    public Annotation[] getAnnotations() {
        Annotation[] annotations = super.getAnnotations();
        if (ObjectUtils.isNotEmpty(annotations)) {
            return annotations;
        }
        Field field = getField();
        if (field != null) {
            return field.getAnnotations();
        }
        return annotations;
    }

    @Override
    public Field getField() {
        if (super.getField() != null) {
            return super.getField();
        }
        MethodParameter methodParameter = super.getMethodParameter();
        if (methodParameter != null && CustomInjectionCoreConfig.getConfig().isEnhanceConstructParameterByFieldAnnotation()) {
            Field field = FieldUtils.getField(methodParameter.getExecutable().getDeclaringClass(), methodParameter.getParameterName(), true);
            return Optional.ofNullable(field)
                .filter(f -> f.getType().isAssignableFrom(methodParameter.getParameterType()))
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .orElse(null);
        }
        return null;
    }
}
