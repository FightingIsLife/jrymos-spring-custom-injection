package cn.jrymos.spring.custom.injection.ccc;

import cn.jrymos.spring.custom.injection.core.BeanNamePrefix;
import cn.jrymos.spring.custom.injection.core.CustomBeanFactory;
import cn.jrymos.spring.custom.injection.core.CustomFactoryMethodParameter;
import cn.jrymos.spring.custom.injection.core.EmptyBeanNamePrefix;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class CccMapFactory extends CustomBeanFactory<CccMap, Map> {

    @SneakyThrows
    @Override
    public Map factoryMethod(CustomFactoryMethodParameter<CccMap> customFactoryMethodParameter) {
        Map map = new LinkedHashMap();
        CccMap annotation = customFactoryMethodParameter.getAnnotation();
        Class<?> elementType = customFactoryMethodParameter.getResolvableType().resolveGeneric(1);
        String[] values = annotation.value();
        if (values.length == 0) {
            Map<String, Object> beans = customFactoryMethodParameter.getBeans(elementType);
            values = beans.keySet().toArray(values);
        }
        BeanNamePrefix beanNamePrefix = annotation.prefixClass() == EmptyBeanNamePrefix.class ? EmptyBeanNamePrefix.EMPTY :
            (BeanNamePrefix) customFactoryMethodParameter.getBean(annotation.prefixClass());
        for (String value : values) {
            Object bean = customFactoryMethodParameter.getBean(beanNamePrefix.toBeanName(value), elementType);
            if (StringUtils.isEmpty(annotation.keyMethod())) {
                map.put(value, bean);
            } else {
                Method method = bean.getClass().getMethod(annotation.keyMethod());
                Object key = method.invoke(bean);
                if (map.containsKey(key) && map.get(key) != bean) {
                    throw new IllegalStateException("Duplicate key " + key);
                }
                map.put(key, bean);
            }
        }
        return map;
    }

    @Override
    public Class<CccMap> getAnnotationType() {
        return CccMap.class;
    }

    @Override
    public void checkAndUpdateCustomFactoryMethodParameter(CustomFactoryMethodParameter<CccMap> customFactoryMethodParameter) {
        if (!customFactoryMethodParameter.getBeanClass().isAssignableFrom(Map.class)) {
            throw new IllegalArgumentException("beanClass must be a Map " + customFactoryMethodParameter.getBeanClass());
        }
    }

    @Override
    public String getBeanValue(CccMap annotation) {
        return String.valueOf(annotation.hashCode());
    }
}
