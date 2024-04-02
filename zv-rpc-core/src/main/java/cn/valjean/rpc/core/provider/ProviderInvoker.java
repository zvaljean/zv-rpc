package cn.valjean.rpc.core.provider;

import cn.valjean.rpc.core.api.RpcRequest;
import cn.valjean.rpc.core.api.RpcResponse;
import cn.valjean.rpc.core.meta.ProviderMeta;
import cn.valjean.rpc.core.utils.TypeUtils;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Optional;

public class ProviderInvoker {

    private MultiValueMap<String, ProviderMeta> skeleton;

    public ProviderInvoker(ProviderBootstrap providerBootstrap) {
        this.skeleton = providerBootstrap.getSkeleton();
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
        if (args == null || args.length == 0)
            return args;
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
