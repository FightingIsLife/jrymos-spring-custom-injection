package cn.jrymos.spring.custom.injection.core;

import com.google.common.base.Preconditions;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;

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
    public abstract Method getFactoryMethod();

    public void checkCustomFactoryMethodParameter(CustomFactoryMethodParameter customFactoryMethodParameter) {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        //校验工厂类定义到了spring
        beanFactory.getBeanDefinition(getName());
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