package cn.valjean.rpc.core.filter;

import cn.valjean.rpc.core.api.Filter;
import cn.valjean.rpc.core.api.RpcRequest;
import cn.valjean.rpc.core.api.RpcResponse;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CacheFilter implements Filter {

    static Map<String, Object> cache = new ConcurrentHashMap<>();

    @Override
    public Object prefilter(RpcRequest request) {
        return cache.get(request.toString());
    }

    @Override
    public Object postfilter(RpcRequest request, RpcResponse response, Object result) {
        cache.putIfAbsent(request.toString(), result);
        return result;
    }
}
