package cn.valjean.rpc.core.provider;

import cn.valjean.rpc.core.annotation.ZVProvider;
import cn.valjean.rpc.core.api.RegistryCenter;
import cn.valjean.rpc.core.meta.InstanceMeta;
import cn.valjean.rpc.core.meta.ProviderMeta;
import cn.valjean.rpc.core.utils.MethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;

@Data
public class ProviderBootstrap implements ApplicationContextAware {

    ApplicationContext applicationContext;

    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();

    private InstanceMeta instance;

    RegistryCenter rc;

    @Value("${server.port}")
    private String port;

    /**
     * 构建服务提供者
     */
    public void init01() {
        // ZVProvider 注解是标注在实现类上的
        Map<String, Object> provides = applicationContext.getBeansWithAnnotation(ZVProvider.class);
        provides.forEach((x, y) -> System.out.println(x));
        for (Object value : provides.values()) {
            for (Class<?> inter : value.getClass().getInterfaces()) {
                for (Method method : inter.getMethods()) {
                    String s = MethodUtils.methodSign(method);
                    if (s.length() == 0) {
                        continue;
                    }
                    createProvider(inter, value, method, s);
                }
            }
        }
    }

    /**
     * https://docs.spring.io/spring-framework/reference/core/beans/factory-nature.html#beans-factory-lifecycle-combined-effects
     * <p>
     * https://docs.spring.io/spring-framework/reference/core/beans/annotation-config/postconstruct-and-predestroy-annotations.html
     * PostConstruct :: 由该注解，则进行初始化的一些操作
     * bean 声明周期
     */
    @PostConstruct
    public void init() {
        rc = applicationContext.getBean(RegistryCenter.class);
        // ZVProvider 注解是标注在实现类上的
        Map<String, Object> provides = applicationContext.getBeansWithAnnotation(ZVProvider.class);
        provides.forEach((x, y) -> System.out.println(x));
        provides.values().forEach(this::getInterfaces);
    }

    private void getInterfaces(Object value) {
        Arrays.stream(value.getClass().getInterfaces())
                .forEach(service -> {
                    for (Method method : service.getMethods()) {
                        String s = MethodUtils.methodSign(method);
                        if (s.length() == 0) {
                            continue;
                        }
                        createProvider(service, value, method, s);
                    }
                });
    }

    @SneakyThrows
    public void start() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        instance = InstanceMeta.http(ip, Integer.valueOf(port));
        rc.start();
        skeleton.keySet().forEach(this::registerService);
    }

    @PreDestroy
    public void stop() {
        System.out.println("PreDestroy-------------------->");
        skeleton.keySet().forEach(this::unregisterService);
        rc.stop();
    }

    private void unregisterService(String service) {
        //        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        rc.unregister(service, instance);
    }

    private void registerService(String service) {
        //        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        rc.register(service, instance);
    }


    /**
     * 根据提供的元数据创建服务提供者
     *
     * @param inter
     * @param obj
     * @param method
     * @param sign
     */
    private void createProvider(Class<?> inter, Object obj, Method method, String sign) {
        ProviderMeta meta = new ProviderMeta();
        meta.setMethod(method);
        meta.setServiceImpl(obj);
        meta.setMethodSign(sign);
        //issue : 这块的obj，是对应的实现类?
        skeleton.add(inter.getCanonicalName(), meta);
    }



}
