package cn.valjean.rpc.core.consumer;

import cn.valjean.rpc.core.api.LoadBalancer;
import cn.valjean.rpc.core.api.RegistryCenter;
import cn.valjean.rpc.core.api.Router;
import cn.valjean.rpc.core.api.RpcContext;
import cn.valjean.rpc.core.meta.InstanceMeta;
import cn.valjean.rpc.core.meta.ServiceMeta;
import cn.valjean.rpc.core.utils.MethodUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class ConsumerBootstrap implements ApplicationContextAware, EnvironmentAware {

    ApplicationContext applicationContext;

    Environment environment;

    private Map<String, Object> stub = new HashMap<>();


    @Value("${app.id}")
    private String app;

    @Value("${app.namespace}")
    private String namespace;

    @Value("${app.env}")
    private String env;


    public void start() {

        Router router = applicationContext.getBean(Router.class);
        LoadBalancer loadBalancer = applicationContext.getBean(LoadBalancer.class);

        RpcContext rpcContext = new RpcContext();
        rpcContext.setLoadBalancer(loadBalancer);
        rpcContext.setRouter(router);

        String[] names = applicationContext.getBeanDefinitionNames();
        long begin = System.currentTimeMillis();
        log.debug("begin => " + begin);

        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);

        // 优化点1：过滤去掉spring/jdk/其他框架本身的bean的反射扫描 TODO 1
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            String packageName = bean.getClass().getPackageName();
            if (bean.getClass().getName().contains("DemoConsumerApplicationTest")) {
                log.debug("packageName = " + packageName);
            }
            if (packageName.startsWith("org.springframework") ||
                    packageName.startsWith("java.") ||
                    packageName.startsWith("javax.") ||
                    packageName.startsWith("jdk.") ||
                    packageName.startsWith("com.fasterxml.") ||
                    packageName.startsWith("com.sun.") ||
                    packageName.startsWith("jakarta.") ||
                    packageName.startsWith("org.apache")) {
                continue;  // 这段逻辑可以降低一半启动速度 300ms->160ms
            }
            log.debug(packageName + " package bean => " + name);

            List<Field> fields = MethodUtils.findAnnotatedField(bean.getClass());
            for (Field field : fields) {
                log.debug("field name = " + field.getName());
                try {
                    // 此处获得是消费者，也就是service
                    Class<?> service = field.getType();
    //                canonical :: 标准
                    String canonicalName = service.getCanonicalName();
                    Object consumer = stub.get(canonicalName);
                    if (consumer == null) {
                        // 没有才创建
                        // fixme: consumer --> null
                        //                        consumer = createConsumer(service, rpcContext, rc);
                        consumer = createFromRegister(service, rpcContext, rc);
                        //将创建好的bean放入其中
                        stub.put(canonicalName, consumer);
                    }
                    field.setAccessible(true);
                    field.set(bean, consumer);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    private Object createFromRegister(Class<?> service, RpcContext rpcContext, RegistryCenter rc) {
        String server = service.getCanonicalName();

        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app)
                .namespace(namespace)
                .env(env)
                .name(server).build();

        List<InstanceMeta> nodes = rc.fetchAll(serviceMeta);

        nodes.forEach(x -> log.debug("providers --->>> " + x));

        rc.subscribe(serviceMeta, event -> {
            nodes.clear();
            nodes.addAll(event.getData());
        });

        return createConsumer(service, rpcContext, nodes);
    }

    //    private List<String> mapUrl(List<String> nodes) {
    //        return nodes.stream().map(x -> "http://" + x.replace('_', ':'))
    //                .collect(Collectors.toList());
    //    }

    /**
     * 创建相关消费者
     * issue 反射相关内容
     *
     * @param service
     * @return
     */
    private Object createConsumer(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        //        List<String> providers = registryCenter.fetchAll(service.getCanonicalName());

        return Proxy.newProxyInstance(service.getClassLoader(),
                // 使用代理来创建consumer，并增强其内容
                new Class[]{service}, new ZVInvocationHandler(service, context, providers));
    }

}
