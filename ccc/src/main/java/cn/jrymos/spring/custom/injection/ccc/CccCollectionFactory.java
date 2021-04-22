package cn.jrymos.spring.custom.injection.ccc;

import cn.jrymos.spring.custom.injection.core.BeanNamePrefix;
import cn.jrymos.spring.custom.injection.core.CustomBeanFactory;
import cn.jrymos.spring.custom.injection.core.CustomFactoryMethodParameter;
import cn.jrymos.spring.custom.injection.core.EmptyBeanNamePrefix;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class CccCollectionFactory extends CustomBeanFactory<CccCollection, Collection> {

    @SneakyThrows
    @Override
    public Collection factoryMethod(CustomFactoryMethodParameter<CccCollection> customFactoryMethodParameter) {
        Class beanClass = customFactoryMethodParameter.getBeanClass();
        Collection collection = getCollection(beanClass);
        CccCollection annotation = customFactoryMethodParameter.getAnnotation();
        BeanNamePrefix beanNamePrefix = annotation.prefixClass() == EmptyBeanNamePrefix.class ? EmptyBeanNamePrefix.EMPTY :
            (BeanNamePrefix) customFactoryMethodParameter.getBean(annotation.prefixClass());
        for (String value : annotation.value()) {
            collection.add(customFactoryMethodParameter.getBean(beanNamePrefix.toBeanName(value)));
        }
        return collection;
    }

    private Collection getCollection(Class beanClass) throws InstantiationException, IllegalAccessException {
        if (beanClass.isAssignableFrom(List.class)) {
            return new ArrayList();
        } else if (beanClass.isAssignableFrom(Set.class)) {
            return new HashSet();
        } else {
            return (Collection) beanClass.newInstance();
        }
    }



    @Override
    public Class<CccCollection> getAnnotationType() {
        return CccCollection.class;
    }

    @Override
    public void checkAndUpdateCustomFactoryMethodParameter(CustomFactoryMethodParameter<CccCollection> customFactoryMethodParameter) {
        if (!Collection.class.isAssignableFrom(customFactoryMethodParameter.getBeanClass())) {
            throw new IllegalArgumentException("beanClass must be a Collection " + customFactoryMethodParameter.getBeanClass());
        }
    }


    @Override
    public String getBeanValue(CccCollection annotation) {
        return String.valueOf(annotation.hashCode());
    }
}
