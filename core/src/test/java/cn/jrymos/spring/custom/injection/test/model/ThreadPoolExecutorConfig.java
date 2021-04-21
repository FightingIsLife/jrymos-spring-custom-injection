package cn.jrymos.spring.custom.injection.test.model;


import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 一个简易的线程池配置注解
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ThreadPoolExecutorConfig {

    int corePoolSize() default 1;

    int maximumPoolSize() default 1;

    long keepAliveTime() default 0;

    TimeUnit timeUnit() default TimeUnit.MINUTES;

    int queueSize() default Integer.MAX_VALUE;

    Class<? extends RejectedExecutionHandler> reject() default ThreadPoolExecutor.AbortPolicy.class;

    /**
     * 唯一标识线程池，允许多个service注入同一个线程池
     */
    String threadPoolId();
}
