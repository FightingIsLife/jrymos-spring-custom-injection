package cn.jrymos.spring.custom.injection.core;

import com.google.common.base.Preconditions;
import lombok.SneakyThrows;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 自定义注解的实例工厂
 * 每次新增自定义注解，只需要继承CustomBeanFactory, 子类应该保证是空的构造函数
 * @param <T> 注解的元注解至少应该有@Target({ElementType.FIELD})、@Retention(RetentionPolicy.RUNTIME)
 */
@Configuration
public abstract class CustomBeanFactory<T extends Annotation, R> implements BeanFactoryPostProcessor, Ordered {

    /**
     * 自定义注解class类型
     */
    public abstract Class<T> getAnnotationType();

    /**
     * 获取支持的注解的value，如果没有value字段或者唯一字段不是value，请重写
     */
    public String getAnnotationValue(T annotation) {
        String value = (String) AnnotationUtils.getAnnotationAttributes(annotation).get("value");
        Preconditions.checkNotNull(value);
        return value;
    }

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
        // 对注解进行校验
        List<T> annotations = customFactoryMethodParameter.getAnnotations();
        T firstAnnotation = customFactoryMethodParameter.getFirstAnnotation();
        Map<String, Object> baseAttributes = AnnotationUtils.getAnnotationAttributes(firstAnnotation);
        for (T annotation : annotations) {
            // 默认所有的属性必须相同
            if (!AnnotationUtils.getAnnotationAttributes(annotation).equals(baseAttributes)) {
                /*
                 * 例如：这种使用方式存在歧义，应该抛出异常
                 * @ThreadPoolExecutorConfig(threadPoolId="aaa", core=1) ...
                 * @ThreadPoolExecutorConfig(threadPoolId="aaa", core=2) ..
                 */
                throw new IllegalArgumentException("not support different attributes");
            }
        }
        customFactoryMethodParameter.setFirstAnnotation(firstAnnotation);
    }

    @Override
    public final void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //校验工厂类定义到了spring
        String myName = getName();
        beanFactory.getBeanDefinition(myName);
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

    /**
     * true 开启业务检查
     */
    protected boolean openCheckDependencyDescriptor() {
        return false;
    }

    /**
     * 检查业务依赖的正确性
     */
    public void checkDependencyDescriptor(DependencyDescriptor descriptor, List<Annotation> annotations) {
        if (!openCheckDependencyDescriptor()) {
            return;
        }
        if (getBeanClass().isAssignableFrom(descriptor.getDeclaredType())
            && annotations.stream().noneMatch(annotation -> getAnnotationType().isInstance(annotation))) {
            throw new UnsupportedOperationException("check failed，must need " + getAnnotationType() + " annotation");
        }
    }

    /**
     * 工厂生成的bean class
     */
    public final Class<?> getBeanClass() {
        return getFactoryMethod().getReturnType();
    }

    /**
     * 工厂生产的bean的name
     */
    public final String getBeanName(T annotation) {
        return this.getName() + "$" + getAnnotationValue(annotation);
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
    public final String getName() {
        String simpleName = this.getClass().getSimpleName();
        int endIndex = simpleName.contains("$") ? simpleName.indexOf("$") : simpleName.length();
        return simpleName.substring(0, 1).toLowerCase() + simpleName.substring(1, endIndex);
    }

    @Override
    public final int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}