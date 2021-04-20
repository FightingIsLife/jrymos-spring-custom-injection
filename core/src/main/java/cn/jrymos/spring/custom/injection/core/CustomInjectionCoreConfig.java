package cn.jrymos.spring.custom.injection.core;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 支持注入特性配置
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class CustomInjectionCoreConfig {

    /**
     * 增强构造器上的参数，使用匹配的字段的注解
     */
    private boolean enhanceConstructParameterByFieldAnnotation = false;
    /**
     * 使用注解自定义注解注入的package前缀，默认全部
     */
    private String packagePrefix = "";
    /**
     * 支持自动扫描注册的CustomBeanFactory，空package不扫描
     */
    private String autoScanCustomBeanFactoryPackage = null;


    @Setter
    @Getter
    private static volatile CustomInjectionCoreConfig config = CustomInjectionCoreConfig.builder()
        .autoScanCustomBeanFactoryPackage(null)
        .packagePrefix(null)
        .enhanceConstructParameterByFieldAnnotation(false)
        .build();
}
