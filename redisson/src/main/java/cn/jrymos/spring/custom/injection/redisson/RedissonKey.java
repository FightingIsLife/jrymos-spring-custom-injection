package cn.jrymos.spring.custom.injection.redisson;

import org.redisson.client.codec.Codec;
import org.redisson.codec.JsonJacksonCodec;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 支持redisson注解的使用方式
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RedissonKey {

    /**
     * redisKey
     */
    String redisKey();

    /**
     * 支持选择codec
     */
    Class<? extends Codec> codec() default JsonJacksonCodec.class;
}
