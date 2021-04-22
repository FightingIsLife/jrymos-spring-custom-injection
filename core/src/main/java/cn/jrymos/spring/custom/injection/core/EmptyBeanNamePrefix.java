package cn.jrymos.spring.custom.injection.core;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class EmptyBeanNamePrefix implements BeanNamePrefix {

    public static final EmptyBeanNamePrefix EMPTY = new EmptyBeanNamePrefix();

    @Override
    public String getBeanNamePrefix() {
        return StringUtils.EMPTY;
    }
}
