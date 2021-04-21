package cn.jrymos.spring.custom.injection.redisson;

import cn.jrymos.spring.custom.injection.core.CustomBeanFactory;
import cn.jrymos.spring.custom.injection.core.CustomFactoryMethodParameter;
import cn.jrymos.spring.custom.injection.core.CustomRootBeanDefinition;
import com.google.common.base.Preconditions;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RObject;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
public class RedissonObjectBeanFactory extends CustomBeanFactory<RedissonKey, RObject> {

    private final Map<Class, Codec> codecMap = new HashMap<>();

    /**
     * redission bean 工厂方法
     * @param parameter bean的定义信息
     * @param redissonClient 依赖的spring管理的redissonClient
     * @return 返回一个实例交给spring管理
     */
    public RObject getRedissionBean(CustomFactoryMethodParameter<RedissonKey> parameter, @Value("${redis.key.prefix}") String redisPrefixKey, RedissonClient redissonClient) {
        RedissonKey key = parameter.getFirstAnnotation();
        Class beanType = parameter.getBeanClass();
        String redisKey = getAndCheckRedisKey(key, redisPrefixKey);
        String simpleName = getAndCheckSimpleName(beanType, redisKey);
        // 拼接redissonClient上的方法名
        String methodName = "get" +  simpleName.substring(1);
        try {
            // 如果配置的是默认的codec，就无需再传codec了
            if (redissonClient instanceof Redisson && key.codec().isInstance(redissonClient.getConfig().getCodec())) {
                Method method = RedissonClient.class.getMethod(methodName, String.class);
                Preconditions.checkArgument(beanType.isAssignableFrom(method.getReturnType()));
                return (RObject) method.invoke(redissonClient, redisPrefixKey + ":" + redisKey);
            } else {
                Method method = RedissonClient.class.getMethod(methodName, String.class, Codec.class);
                Preconditions.checkArgument(beanType.isAssignableFrom(method.getReturnType()));
                return (RObject) method.invoke(redissonClient, redisPrefixKey + ":" + redisKey, getCodec(key.codec()));
            }
        } catch (Exception e) {
            log.error("no such method:{}, {}", key, beanType, e);
            throw new UnsupportedOperationException(e);
        }
    }

    private String getAndCheckRedisKey(RedissonKey key, @Value("${redis.key.prefix}") String redisPrefixKey) {
        String redisKey = key.redisKey();
        if (StringUtils.isEmpty(redisPrefixKey)) {
            log.error("can not config wumii.redis.key.prefix empty, redisKey:{}", redisKey);
            throw new IllegalArgumentException("can not config wumii.redis.key.prefix empty, " + redisKey);
        }
        return redisKey;
    }

    private String getAndCheckSimpleName(Class beanType, String redisKey) {
        String simpleName = beanType.getSimpleName();
        // 说明：redissonClient上的方法都是以 getXxx, 其中Xxx 是 beanType 的类型减去首字符R，如果class名称不是以R开头不支持
        if (!simpleName.startsWith("R")) {
            log.error("can not config wumii.redis.key.prefix empty, redisKey:{}, name:{}", redisKey, simpleName);
            throw new UnsupportedOperationException("can not found get method:" + simpleName);
        }
        return simpleName;
    }

    private synchronized Codec getCodec(Class<? extends Codec> codecClass) throws IllegalAccessException, InstantiationException {
        Codec codec = codecMap.get(codecClass);
        if (codec == null) {
            codec = codecClass.newInstance();
            codecMap.put(codecClass, codec);
        }
        return codec;
    }

    @Override
    public Class<RedissonKey> getAnnotationType() {
        return RedissonKey.class;
    }

    @Override
    public String getAnnotationValue(RedissonKey annotation) {
        return annotation.redisKey();
    }

    @SneakyThrows
    @Override
    public Method getFactoryMethod() {
        return getClass().getMethod("getRedissionBean", CustomRootBeanDefinition.class, String.class, RedissonClient.class);
    }

    @Override
    public boolean openCheckDependencyDescriptor() {
        return true;
    }
}
