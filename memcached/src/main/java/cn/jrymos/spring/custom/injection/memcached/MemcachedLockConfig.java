package cn.jrymos.spring.custom.injection.memcached;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 支持MemcachedLock注解的使用方式
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface MemcachedLockConfig {
    String identify();
    int expSecs() default -1; //see MemcachedLockFactory
}
