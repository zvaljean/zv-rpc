package cn.valjean.rpc.core.consumer;

import cn.valjean.rpc.core.api.Filter;
import cn.valjean.rpc.core.api.RpcContext;
import cn.valjean.rpc.core.api.RpcRequest;
import cn.valjean.rpc.core.api.RpcResponse;
import cn.valjean.rpc.core.consumer.http.OkHttpInvoker;
import cn.valjean.rpc.core.meta.InstanceMeta;
import cn.valjean.rpc.core.utils.MethodUtils;
import cn.valjean.rpc.core.utils.TypeUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;

@Slf4j
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
        //        prefilter
        List<Filter> filters = context.getFilters();
        for (Filter filter : filters) {
            Object preResult = filter.prefilter(rpcRequest);
            if (preResult != null) {
                log.debug("prefilter --> name : {}, result: {}", filter.getClass().getName(), preResult);
                return preResult;
            }
        }

        RpcResponse rpcResponse = httpInvoker.post(rpcRequest, instance.toUrl());
        Object result = TypeUtils.caseMethodResult(method, rpcResponse.getData());

        //        post filter
        for (Filter filter : filters) {
            Object filterResult = filter.postfilter(rpcRequest, rpcResponse, result);
            if (filterResult != null) {
                log.debug("post filter --> name : {}, result: {}", filter.getClass().getName(), filter);
                return filterResult;
            }
        }

        // success
        if (rpcResponse.isStatus())
            return result;
        else {
            Exception ex = rpcResponse.getEx();
            throw new RuntimeException(ex);
        }
    }

}
