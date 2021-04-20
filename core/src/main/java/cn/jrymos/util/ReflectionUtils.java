package cn.jrymos.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@UtilityClass
public class ReflectionUtils {

    private static final String RESOURCE_PATTERN = "/**/*.class";
    private static final ResourcePatternResolver resourcePatternResolver
        = ResourcePatternUtils.getResourcePatternResolver(new DefaultResourceLoader());
    private static final CachingMetadataReaderFactory cachingMetadataReaderFactory
        = new CachingMetadataReaderFactory(resourcePatternResolver);

    /**
     * 获取某个类所在包的所有类
     *
     * @param packageName 包名
     * @return 类列表
     */
    public static List<Class<?>> getPackageClass(String packageName) {
        String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(packageName) + RESOURCE_PATTERN;
        try {
            Resource[] resources = resourcePatternResolver.getResources(pattern);
            List<Class<?>> classes = new ArrayList<>();
            for (Resource resource: resources) {
                if (resource.isReadable()) {
                    MetadataReader reader = cachingMetadataReaderFactory.getMetadataReader(resource);
                    String className = reader.getClassMetadata().getClassName();
                    try {
                        Class<?> clazz = Class.forName(className);
                        classes.add(clazz);
                    } catch (Throwable e) {
                    }
                }
            }
            return classes;
        } catch (IOException e) {
            log.error("getPackageClass error:" + packageName);
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * 获取给定类型所在包的所有子类
     *
     * @param baseClass 基准类
     * @return 子类列表
     */
    public static List<Class<?>> getPackageChildClass(String packageName, Class<?> baseClass) {
        List<Class<?>> classes = getPackageClass(packageName);
        return classes.stream().filter(v -> baseClass.isAssignableFrom(v) && !baseClass.getName().equals(v.getName()))
            .collect(Collectors.toList());
    }

    /**
     * 获取方法上含有annotationType注解的参数
     */
    public static List<Parameter> getParametersListWithAnnotation(Executable executable, Class<Annotation> annotationType) {
        return getParametersListWithAnnotation(executable.getParameters(), annotationType);
    }

    /**
     * 获取方法上含有annotationType注解的参数
     */
    public static List<Parameter> getParametersListWithAnnotation(Parameter[] parameters, Class<Annotation> annotationType) {
        if (ObjectUtils.isEmpty(parameters)) {
            return Collections.emptyList();
        }
        return Arrays.stream(parameters)
            .filter(parameter -> !ObjectUtils.isEmpty(parameter.getAnnotations()))
            .filter(parameter -> Arrays.stream(parameter.getAnnotations()).map(Annotation::annotationType).anyMatch(annotationType::equals))
            .collect(Collectors.toList());
    }
}
