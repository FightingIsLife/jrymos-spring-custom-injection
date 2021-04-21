package cn.jrymos.spring.custom.injection.threadpool;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ThreadPoolConfig {

    /**
     * @see ThreadPoolExecutor#getCorePoolSize()
     */
    int coreS() default 1;

    /**
     * @see ThreadPoolExecutor#getMaximumPoolSize()
     */
    int maxS() default 1;

    /**
     * @see ThreadPoolExecutor#getKeepAliveTime(TimeUnit)
     */
    long seconds() default 0;


    /**
     * @see java.util.concurrent.SynchronousQueue qSize==0
     * @see java.util.concurrent.LinkedBlockingQueue qSize >= 1000
     * @see java.util.concurrent.ArrayBlockingQueue qSize < 1000
     * @return
     */
    int qSize() default Integer.MAX_VALUE;

    /**
     * @see ThreadPoolExecutor#getRejectedExecutionHandler()
     */
    Class<? extends RejectedExecutionHandler> reject() default ThreadPoolExecutor.AbortPolicy.class;

    /**
     * 线程池id
     */
    String id();
}
