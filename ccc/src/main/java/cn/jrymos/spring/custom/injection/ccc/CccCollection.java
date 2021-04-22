package cn.jrymos.spring.custom.injection.ccc;

import cn.jrymos.spring.custom.injection.core.BeanNamePrefix;
import cn.jrymos.spring.custom.injection.core.EmptyBeanNamePrefix;
import cn.jrymos.spring.custom.injection.core.Multiple;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Multiple
public @interface CccCollection {
    /**
     * 集合依赖的bean名称，默认是空集合
     */
    String[] value() default {};

    /**
     * 用来支持value内容简写
     */
    Class<? extends BeanNamePrefix> prefixClass() default EmptyBeanNamePrefix.class;
}
