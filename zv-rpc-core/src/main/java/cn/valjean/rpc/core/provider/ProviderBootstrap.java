package cn.valjean.rpc.core.provider;

import cn.valjean.rpc.core.annotation.ZVProvider;
import cn.valjean.rpc.core.api.RegistryCenter;
import cn.valjean.rpc.core.api.RpcRequest;
import cn.valjean.rpc.core.api.RpcResponse;
import cn.valjean.rpc.core.meta.ProviderMeta;
import cn.valjean.rpc.core.utils.MethodUtils;
import cn.valjean.rpc.core.utils.TypeUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Data
public class ProviderBootstrap implements ApplicationContextAware {

    ApplicationContext applicationContext;

    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();

    private String instance;

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

    @PostConstruct
    public void init() {
        // ZVProvider 注解是标注在实现类上的
        Map<String, Object> provides = applicationContext.getBeansWithAnnotation(ZVProvider.class);
        provides.forEach((x, y) -> System.out.println(x));
        provides.values().forEach(this::getInterfaces);
    }

    private void getInterfaces(Object value) {
        Arrays.stream(value.getClass().getInterfaces())
                .forEach(iter -> {
                    for (Method method : iter.getMethods()) {
                        String s = MethodUtils.methodSign(method);
                        if (s.length() == 0) {
                            continue;
                        }
                        createProvider(iter, value, method, s);
                    }
                });
    }

    @SneakyThrows
    public void start() {
        String ip = InetAddress.getLocalHost().getHostAddress();
        instance = ip + "_" + port;
        skeleton.keySet().forEach(this::registerService);
    }

    @PreDestroy
    public void stop() {
        skeleton.keySet().forEach(this::unregisterService);
    }

    private void unregisterService(String service) {
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        rc.unregister(service, instance);
    }

    private void registerService(String service) {
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
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

    /**
     * 调用对应的服务提供者方法
     *
     * @param request
     * @return
     */
    public RpcResponse<Object> invoke(RpcRequest request) {
        RpcResponse response = new RpcResponse();
        List<ProviderMeta> providerMetas = skeleton.get(request.getService());
        try {
            ProviderMeta meta = findProviderMeta(providerMetas, request.getMethodSign());
//            Method method = MethodUtils.findMethod(bean.getClass(), request.getMethodSign());
            Method method = meta.getMethod();
            Object[] args = processArgs(request.getArgs(), method.getParameterTypes());
            Object result = method.invoke(meta.getServiceImpl(), args);
            response.setStatus(true);
            response.setData(result);
            return response;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * 对消费者的参数进行处理
     *
     * @param args
     * @param parameterTypes
     * @return
     */
    private Object[] processArgs(Object[] args, Class<?>[] parameterTypes) {
        if (args == null || args.length == 0) return args;
        Object[] param = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            param[i] = TypeUtils.cast(args[i], parameterTypes[i]);
        }
        return param;
    }

    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        // issue  Optional
        Optional<ProviderMeta> first
                = providerMetas.stream().filter(x -> x.getMethodSign().equals(methodSign)).findFirst();
        return first.orElse(null);

//        for (ProviderMeta providerMeta : providerMetas) {
//            if (methodSign.equals(providerMeta.getMethodSign())) {
//                return providerMeta;
//            }
//        }
//        return null;
    }


}
