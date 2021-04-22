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
public @interface CccMap {

    /**
     * map依赖的bean名称， 空表示根据map的value泛型类型匹配（而不是注入空map）
     */
    String[] value() default {};

    /**
     * map key的获取方法，默认是bean name
     */
    String keyMethod() default "";

    //用来支持value内容简写
    Class<? extends BeanNamePrefix> prefixClass() default EmptyBeanNamePrefix.class;
}
