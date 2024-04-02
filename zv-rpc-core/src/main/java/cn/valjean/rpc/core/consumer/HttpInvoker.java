package cn.valjean.rpc.core.consumer;

import cn.valjean.rpc.core.api.RpcRequest;
import cn.valjean.rpc.core.api.RpcResponse;

public interface HttpInvoker {
     RpcResponse<?> post(RpcRequest rpcRequest, String url);
}
