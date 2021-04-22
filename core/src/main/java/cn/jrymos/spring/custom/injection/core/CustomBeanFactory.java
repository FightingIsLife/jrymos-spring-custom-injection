package cn.jrymos.spring.custom.injection.core;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 自定义注解的实例工厂
 * 每次新增自定义注解，只需要继承CustomBeanFactory, 子类应该保证是空的构造函数,子类可以通过@Configuration注册到spring中，也可以调用CustomBeanFactoryRegister.register注册
 * 相关demo:
 * @see ThreadPoolFactory
 * @see RedissonObjectFactory
 * @see MemcachedLockFactory
 * @see CustomBeanFactoryRegister#register(CustomBeanFactory)
 * @param <T> 注解的元注解至少应该有@Target({ElementType.FIELD})、@Retention(RetentionPolicy.RUNTIME)
 */
@Slf4j
public abstract class CustomBeanFactory<T extends Annotation, R> implements BeanFactoryPostProcessor, BeanNamePrefix, Ordered {

    /**
     * 自定义注解class类型
     */
    public abstract Class<T> getAnnotationType();

    /**
     * 获取bean标识的值
     * such as:
     * @see ThreadPoolConfig#id
     * @see CccCollection#hashcode
     * @see RedissonKey#redisKey
     * @see MemcachedLockConfig#identify
     */
    public abstract String getBeanValue(T annotation);

    /**
     * 获取工厂方法， 支持重写以传递更多的参数（只要是spring bean都支持传，无需做额外处理）
     * 工厂方法需要满足的条件：
     * @see CustomFactoryMethodParameter 第一个参数是CustomFactoryMethodParameter
     * @see this#getBeanClass() 返回结果是要生产的bean实例
     * @see cn.jrymos.spring.custom.injection.redisson.RedissonObjectBeanFactory 重写了getFactoryMethod，传RedissonClient
     */
    @SneakyThrows
    public Method getFactoryMethod() {
        return getClass().getMethod("factoryMethod", CustomFactoryMethodParameter.class);
    }

    /**
     * 如果没有重写getFactoryMethod，那就一定要重写factoryMethod
     * 如果重写了getFactoryMethod，那就没有任何必要重写factoryMethod了
     */
    public R factoryMethod(CustomFactoryMethodParameter<T> customFactoryMethodParameter) {
        //such as: return customFactoryMethodParameter.getBeanClass().newInstance();
        throw new UnsupportedOperationException("not implements");
    }


    /**
     * 校验和设置customFactoryMethodParameter相关属性
     */
    public void checkAndUpdateCustomFactoryMethodParameter(CustomFactoryMethodParameter<T> customFactoryMethodParameter) {
        // class类型检查
        Class<?> modelClass = getBeanClass();
        for (Class<?> beanClass : customFactoryMethodParameter.getBeanClasses()) {
            if (!modelClass.isAssignableFrom(beanClass) && !beanClass.isAssignableFrom(modelClass)) {
                throw new IllegalArgumentException("not support different class " + modelClass + ", " + beanClass);
            }
        }
        // 对注解进行校验
        List<T> annotations = customFactoryMethodParameter.getAnnotations();
        T firstAnnotation = customFactoryMethodParameter.getAnnotation();
        Map<String, Object> baseAttributes = AnnotationUtils.getAnnotationAttributes(firstAnnotation);
        for (T annotation : annotations) {
            // 默认所有的属性必须相同
            if (!AnnotationUtils.getAnnotationAttributes(annotation).equals(baseAttributes)) {
                /*
                 * 例如：这种使用方式存在歧义，应该抛出异常
                 * @ThreadPoolExecutorConfig(threadPoolId="aaa", core=1) ...
                 * @ThreadPoolExecutorConfig(threadPoolId="aaa", core=2) ..
                 */
                throw new IllegalArgumentException("not support different attributes " + annotation + ", " + firstAnnotation);
            }
        }
        customFactoryMethodParameter.setFirstAnnotation(firstAnnotation);
    }


    /**
     * 工厂生成的bean class
     */
    public final Class<?> getBeanClass() {
        return getFactoryMethod().getReturnType();
    }

    /**
     * 是否是注入集合类的工厂
     * @see CccCollection
     * @see CccMap
     * @see CccCollectionFactory
     * @see CccMapFactory
     */
    public final boolean isMultipleFactory() {
        return getAnnotationType().getAnnotation(Multiple.class) != null;
    }

    /**
     * 工厂生产的bean的name
     */
    public final String getBeanName(T annotation) {
        return getBeanNamePrefix() + getBeanValue(annotation);
    }

    public final String getBeanNamePrefix() {
        return getBeanNamePrefix(getClass());
    }

    /**
     * 生成bean的工厂方法名称
     */
    public final String getFactoryMethodName() {
        return getFactoryMethod().getName();
    }

    /**
     * 工厂注册到spring的beanName
     */
    public final String getFactoryName() {
        return getFactoryName(getClass());
    }

    @Override
    public final int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }


    @Override
    public final void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //校验工厂类注册到了spring
        String myName = getFactoryName();
        beanFactory.getBean(myName);
        //校验customFactoryMethodParameter
        for (String beanDefinitionName : beanFactory.getBeanDefinitionNames()) {
            BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanDefinitionName);
            if (beanDefinition instanceof CustomRootBeanDefinition && myName.equals(beanDefinition.getFactoryBeanName())) {
                CustomFactoryMethodParameter parameter = (CustomFactoryMethodParameter) beanDefinition.getConstructorArgumentValues()
                    .getArgumentValue(0, CustomFactoryMethodParameter.class).getValue();
                checkAndUpdateCustomFactoryMethodParameter(parameter);
            }
        }
    }


    public static String getFactoryName(Class<? extends CustomBeanFactory> clazz) {
        String simpleName = clazz.getSimpleName();
        // 匿名内部类
        if (StringUtils.isEmpty(simpleName)) {
            log.warn("not found class simple name : {}", clazz);
            String name = CustomBeanFactory.class.getSimpleName() + "$" + clazz.hashCode();
            return name.substring(0, 1).toLowerCase() + name.substring(1);
        }
        int endIndex = simpleName.contains("$") ? simpleName.indexOf("$") : simpleName.length();
        return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1, endIndex);
    }

    public static String getBeanNamePrefix(Class<? extends CustomBeanFactory> clazz) {
        return getFactoryName(clazz) + "$-";
    }
}