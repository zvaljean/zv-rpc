package cn.valjean.rpc.core.consumer;

import cn.valjean.rpc.core.api.RpcContext;
import cn.valjean.rpc.core.api.RpcRequest;
import cn.valjean.rpc.core.api.RpcResponse;
import cn.valjean.rpc.core.consumer.http.OkHttpInvoker;
import cn.valjean.rpc.core.meta.InstanceMeta;
import cn.valjean.rpc.core.utils.MethodUtils;
import cn.valjean.rpc.core.utils.TypeUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

public class ZVInvocationHandler implements InvocationHandler {


    Class<?> service;
    RpcContext context;
    List<InstanceMeta> provides;

    HttpInvoker httpInvoker = new OkHttpInvoker();

    public ZVInvocationHandler(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        this.service = service;
        this.context = context;
        this.provides = providers;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (MethodUtils.checkLocalMethod(method.getName())) {
            return null;
        }

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethodSign(MethodUtils.methodSign(method));
        rpcRequest.setArgs(args);

        List<InstanceMeta> instances = context.getRouter().route(provides);
        InstanceMeta instance = (InstanceMeta) context.getLoadBalancer().choose(instances);

        RpcResponse rpcResponse = httpInvoker.post(rpcRequest, instance.toUrl());
        // success
        if (rpcResponse.isStatus())
            return TypeUtils.caseMethodResult(method, rpcResponse.getData());
        else {
            Exception ex = rpcResponse.getEx();
            throw new RuntimeException(ex);
        }
    }

}
