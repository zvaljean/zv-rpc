package cn.valjean.rpc.core.provider;

import cn.valjean.rpc.core.annotation.ZVProvider;
import cn.valjean.rpc.core.api.RegistryCenter;
import cn.valjean.rpc.core.meta.InstanceMeta;
import cn.valjean.rpc.core.meta.ProviderMeta;
import cn.valjean.rpc.core.meta.ServiceMeta;
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

    @Value("${app.id}")
    private String app;

    @Value("${app.namespace}")
    private String namespace;

    @Value("${app.env}")
    private String env;


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
                    String sign = MethodUtils.methodSign(method);
                    if (sign.length() == 0) {
                        continue;
                    }
                    createProvider(inter, value, method, sign);
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

    private void getInterfaces(Object impl) {
        Arrays.stream(impl.getClass().getInterfaces()).forEach(service -> {
            for (Method method : service.getMethods()) {
                String sign = MethodUtils.methodSign(method);
                if (sign.length() == 0) {
                    continue;
                }
                createProvider(service, impl, method, sign);
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
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app)
                .namespace(namespace)
                .env(env)
                .name(service).build();


        rc.unregister(serviceMeta, instance);
    }

    private void registerService(String service) {
        //        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app)
                .namespace(namespace)
                .env(env)
                .name(service).build();
        rc.register(serviceMeta, instance);
    }


    /**
     * 根据提供的元数据创建服务提供者
     *
     * @param service
     * @param impl
     * @param method
     * @param sign
     */
    private void createProvider(Class<?> service, Object impl, Method method, String sign) {
        ProviderMeta meta = ProviderMeta.builder()
                .method(method)
                .serviceImpl(impl)
                .serviceImpl(sign).build();
        //issue : 这块的obj，是对应的实现类?
        skeleton.add(service.getCanonicalName(), meta);
    }


}
