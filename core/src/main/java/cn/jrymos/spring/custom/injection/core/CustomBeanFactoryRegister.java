package cn.jrymos.spring.custom.injection.core;

import cn.jrymos.util.ReflectionUtils;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;


import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 管理所有的CustomBeanFactory
 */
@UtilityClass
public class CustomBeanFactoryRegister {

    private static volatile List<CustomBeanFactory<Annotation>> FACTORIES;

    static {
        getFactories();
    }

    @SneakyThrows
    public static List<CustomBeanFactory<Annotation>> getFactories() {
        if (FACTORIES != null) {
            return FACTORIES;
        }
        synchronized (CustomBeanFactoryRegister.class) {
            if (FACTORIES != null) {
                return FACTORIES;
            }
            List<Class<?>> packageChildClass = ReflectionUtils.getPackageChildClass(CustomInjectionCoreConfig.getAutoScanCustomBeanFactoryPackage(), CustomBeanFactory.class);
            List<CustomBeanFactory<Annotation>> factories = packageChildClass.stream()
                .map(c -> (Class<? extends CustomBeanFactory>) c)
                .map(CustomBeanFactoryRegister::newCustomBeanFactory)
                .collect(Collectors.toList());
            FACTORIES = Collections.unmodifiableList(factories);
        }
        return FACTORIES;
    }

    /**
     * 注册一个customBeanFactory
     */
    public synchronized static void register(CustomBeanFactory<Annotation> customBeanFactory) {
        List<CustomBeanFactory<Annotation>> factories = getFactories();
        if (factories.contains(customBeanFactory)) {
            return;
        }
        factories = new ArrayList<>(factories);
        factories.add(customBeanFactory);
        FACTORIES = Collections.unmodifiableList(factories);
    }

    /**
     * 根据注解获取有且唯一的CustomBeanFactory
     */
    public static CustomBeanFactory<Annotation> getUnique(Annotation annotation) {
        List<CustomBeanFactory<Annotation>> beanFactories = FACTORIES.stream()
                .filter(customBeanFactory -> customBeanFactory.getAnnotationType().isInstance(annotation))
                .collect(Collectors.toList());
        if (beanFactories.size() > 1) {
            throw new IllegalStateException("not unique factory " + beanFactories);
        }
        if (beanFactories.isEmpty()) {
            throw new IllegalStateException("not found annotation " + annotation);
        }
        return beanFactories.get(0);
    }

    @SneakyThrows
    private static CustomBeanFactory<Annotation> newCustomBeanFactory(Class<? extends CustomBeanFactory> clazz) {
        return clazz.newInstance();
    }

}
