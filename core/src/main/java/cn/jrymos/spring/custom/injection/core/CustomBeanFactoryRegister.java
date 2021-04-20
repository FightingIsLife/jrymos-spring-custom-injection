package cn.jrymos.spring.custom.injection.core;

import cn.jrymos.util.ReflectionUtils;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;


import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 管理所有的CustomBeanFactory
 */
@UtilityClass
@Slf4j
public class CustomBeanFactoryRegister {

    private static volatile Map<Class<? extends Annotation>, CustomBeanFactory<Annotation>> FACTORIES;

    static {
        getFactories();
    }

    @SneakyThrows
    public static Collection<CustomBeanFactory<Annotation>> getFactories() {
        if (FACTORIES == null) {
            initFactories();
        }
        return FACTORIES.values();
    }

    /**
     * 初始化 FACTORIES
     */
    private synchronized static void initFactories() {
        if (FACTORIES != null) {
            return;
        }
        if (StringUtils.isNotEmpty(CustomInjectionCoreConfig.getAutoScanCustomBeanFactoryPackage())) {
            List<Class<?>> packageChildClass = ReflectionUtils.getPackageChildClass(CustomInjectionCoreConfig.getAutoScanCustomBeanFactoryPackage(), CustomBeanFactory.class);
            Map<Class<? extends Annotation>, CustomBeanFactory<Annotation>> factories = packageChildClass.stream()
                .map(c -> (Class<? extends CustomBeanFactory>) c)
                .map(CustomBeanFactoryRegister::newCustomBeanFactory)
                .collect(Collectors.toMap(CustomBeanFactory::getAnnotationType, Function.identity()));
            FACTORIES = Collections.unmodifiableMap(factories);
        } else {
            FACTORIES = Collections.emptyMap();
        }
    }

    /**
     * 注册一个customBeanFactory
     */
    public synchronized static void register(CustomBeanFactory<Annotation> customBeanFactory) {
        if (FACTORIES == null) {
            initFactories();
        }
        if (FACTORIES.containsKey(customBeanFactory.getAnnotationType())) {
            CustomBeanFactory<Annotation> sameAnnotationBeanFactory = FACTORIES.get(customBeanFactory.getAnnotationType());
            if (sameAnnotationBeanFactory.getClass() == customBeanFactory.getClass()) {
                log.info("already register " + customBeanFactory);
                return;
            } else {
                throw new IllegalStateException("not unique factory " + sameAnnotationBeanFactory + "," + customBeanFactory);
            }
        }
        // 使用copy on write机制更新
        Map<Class<? extends Annotation>, CustomBeanFactory<Annotation>> map = Maps.newHashMap(FACTORIES);
        map.put(customBeanFactory.getAnnotationType(), customBeanFactory);
        FACTORIES = Collections.unmodifiableMap(map);
    }

    /**
     * 根据注解获取有且唯一的CustomBeanFactory
     */
    public static CustomBeanFactory<Annotation> getUnique(Annotation annotation) {
        CustomBeanFactory<Annotation> beanFactory = FACTORIES.get(annotation.annotationType());
        if (beanFactory == null) {
            throw new IllegalStateException("not found annotation " + annotation);
        }
        return beanFactory;
    }

    @SneakyThrows
    private static CustomBeanFactory<Annotation> newCustomBeanFactory(Class<? extends CustomBeanFactory> clazz) {
        return clazz.newInstance();
    }

}
