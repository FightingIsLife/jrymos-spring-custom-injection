package cn.jrymos.spring.custom.injection.core;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.CustomAutowireConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

/**
 * 将CustomContextAnnotationAutowireCandidateResolver初始化到DefaultListableBeanFactory
 */
@Slf4j
@Configuration
public class CustomAnnotationApplicationContextInitializer implements ApplicationContextInitializer {
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (applicationContext.getBeanFactory() instanceof DefaultListableBeanFactory) {
            CustomContextAnnotationAutowireCandidateResolver autowireCandidateResolver = new CustomContextAnnotationAutowireCandidateResolver();
            ((DefaultListableBeanFactory) applicationContext.getBeanFactory()).setAutowireCandidateResolver(autowireCandidateResolver);
            CustomAutowireConfigurer postProcessor = new CustomAutowireConfigurer();
            postProcessor.setCustomQualifierTypes(autowireCandidateResolver.getCustomQualifierTypes());
            log.info("init autowireCandidateResolver,{},{}", autowireCandidateResolver, autowireCandidateResolver.getCustomQualifierTypes());
            applicationContext.addBeanFactoryPostProcessor(postProcessor);
        }
    }
}
