package cn.jrymos.spring.custom.injection.core;

/**
 * bean实例名称的前缀
 */
public interface BeanNamePrefix {

    String getBeanNamePrefix();

    default String toBeanName(String value) {
        return getBeanNamePrefix() + value;
    }
}
