package cn.jrymos.spring.custom.injection.ccc;

import cn.jrymos.spring.custom.injection.core.CustomBeanFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Configuration;


@Configuration
@Slf4j
public class CccBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (beanName.startsWith(CustomBeanFactory.getBeanNamePrefix(CccCollectionFactory.class))) {
            log.info("this is CccCollectionFactory produce collection {},{}", beanName, bean);
        }
        if (beanName.startsWith(CustomBeanFactory.getBeanNamePrefix(CccMapFactory.class))) {
            log.info("this is CccMapFactory produce map {},{}", beanName, bean);
        }
        return bean;
    }
}
