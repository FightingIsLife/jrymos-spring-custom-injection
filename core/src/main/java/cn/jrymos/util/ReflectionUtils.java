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

import java.io.IOException;
import java.util.ArrayList;
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
                    Class<?> clazz = Class.forName(className);
                    classes.add(clazz);
                }
            }
            return classes;
        } catch (ClassNotFoundException | IOException e) {
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
}
