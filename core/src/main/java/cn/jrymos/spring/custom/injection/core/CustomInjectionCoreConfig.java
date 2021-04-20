package cn.jrymos.spring.custom.injection.core;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

/**
 * 支持注入特性配置
 */
@UtilityClass
public class CustomInjectionCoreConfig {

    /**
     * 增强构造器上的参数，使用匹配的字段的注解
     */
    @Setter
    @Getter
    private static boolean enhanceConstructParameterByFieldAnnotation = false;

    /**
     * 使用注解自定义注解注入的package前缀，默认全部
     */
    @Setter
    @Getter
    private static String PackagePrefix = "";

    /**
     * 支持自动扫描注册的CustomBeanFactory
     */
    @Setter
    @Getter
    private static String autoScanCustomBeanFactoryPackage = "";
}
