package cn.valjean.rpc.core.provider;

import cn.valjean.rpc.core.annotation.ZVProvider;
import cn.valjean.rpc.core.api.RpcRequest;
import cn.valjean.rpc.core.api.RpcResponse;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Data
public class ProviderBootstrap implements ApplicationContextAware {

    ApplicationContext applicationContext;

    private Map<String, Object> skeleton = new HashMap<>();

    @PostConstruct
    public void buildProviders() {
        Map<String, Object> provides = applicationContext.getBeansWithAnnotation(ZVProvider.class);
        provides.forEach((x, y) -> System.out.println(x));
        provides.values().forEach(x -> {
            Class<?> anInterface = x.getClass().getInterfaces()[0];
            skeleton.put(anInterface.getCanonicalName(), x);
        });
    }

    public RpcResponse<Object> invoke(RpcRequest request) {
        Object bean = skeleton.get(request.getService());
        try {
            Method method = findMethod(bean.getClass(), request.getMethod());
            Object result = method.invoke(bean, request.getArgs());
            return new RpcResponse<>(true, result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public Method findMethod(Class<?> aclas, String methodName) {
        for (Method method : aclas.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }

}
