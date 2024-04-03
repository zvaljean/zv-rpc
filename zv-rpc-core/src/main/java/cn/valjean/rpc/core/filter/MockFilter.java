package cn.valjean.rpc.core.filter;

import cn.valjean.rpc.core.api.Filter;
import cn.valjean.rpc.core.api.RpcRequest;
import cn.valjean.rpc.core.api.RpcResponse;
import cn.valjean.rpc.core.utils.MethodUtils;
import cn.valjean.rpc.core.utils.MockUtils;
import lombok.SneakyThrows;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MockFilter implements Filter {

    @SneakyThrows
    @Override
    public Object prefilter(RpcRequest request) {
        Class<?> service = Class.forName(request.getService());
        Method method = findMethod(service, request.getMethodSign());
        Class<?> returnType = method.getReturnType();
        return MockUtils.mock(returnType);
    }

    private Method findMethod(Class service, String methodSign) {
        return Arrays.stream(service.getMethods())
                .filter(method -> !MethodUtils.checkLocalMethod(method))
                .filter(method -> methodSign.equals(MethodUtils.methodSign(method)))
                .findFirst().orElse(null);

    }

    @Override
    public Object postfilter(RpcRequest request, RpcResponse response, Object result) {
        return null;
    }
}
