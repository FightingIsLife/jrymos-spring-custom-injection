package cn.jrymos.spring.custom.injection.threadpool;

import cn.jrymos.spring.custom.injection.core.CustomBeanFactory;

import java.util.concurrent.ThreadPoolExecutor;

public class ThreadPoolFactory extends CustomBeanFactory<ThreadPoolConfig, ThreadPoolExecutor> {



    @Override
    public Class<ThreadPoolConfig> getAnnotationType() {
        return ThreadPoolConfig.class;
    }
}
