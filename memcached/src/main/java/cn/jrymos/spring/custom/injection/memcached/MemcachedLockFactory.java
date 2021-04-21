package cn.jrymos.spring.custom.injection.memcached;

import cn.jrymos.spring.custom.injection.core.CustomBeanFactory;
import cn.jrymos.spring.custom.injection.core.CustomFactoryMethodParameter;
import com.whalin.MemCached.MemCachedClient;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class MemcachedLockFactory extends CustomBeanFactory<MemcachedLockConfig, MemcachedLock> {

    @SneakyThrows
    @Override
    public Method getFactoryMethod() {
        return getClass().getMethod("getMemcachedLock", CustomFactoryMethodParameter.class, MemCachedClient.class);
    }

    public MemcachedLock getMemcachedLock(CustomFactoryMethodParameter<MemcachedLockConfig> parameter, MemCachedClient memCachedClient) {
        MemcachedLockConfig annotation = parameter.getFirstAnnotation();// see checkAndUpdateCustomFactoryMethodParameter
        return new MemcachedLock(memCachedClient, annotation.identify(), annotation.expSecs() <= 0 ? 10 : annotation.expSecs());
    }

    @Override
    public void checkAndUpdateCustomFactoryMethodParameter(CustomFactoryMethodParameter<MemcachedLockConfig> customFactoryMethodParameter) {
        List<MemcachedLockConfig> annotations = customFactoryMethodParameter.getAnnotations();
        List<MemcachedLockConfig> memcachedLockConfigs = annotations.stream()
            .filter(memcachedLockConfig -> memcachedLockConfig.expSecs() > 0).collect(Collectors.toList());
        if (memcachedLockConfigs.size() > 1) {
            throw new IllegalArgumentException("not support different attributes " + memcachedLockConfigs);
        }
        customFactoryMethodParameter.setFirstAnnotation(!memcachedLockConfigs.isEmpty() ? memcachedLockConfigs.get(0) : null);
    }

    @Override
    public Class<MemcachedLockConfig> getAnnotationType() {
        return MemcachedLockConfig.class;
    }

    @Override
    public String getAnnotationValue(MemcachedLockConfig annotation) {
        return annotation.identify();
    }
}
