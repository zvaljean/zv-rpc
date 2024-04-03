package cn.valjean.rpc.core.api;

/**
 * 过滤特定请求
 */
public interface Filter {

    Object prefilter(RpcRequest request);

    Object postfilter(RpcRequest request, RpcResponse response, Object result);

    Filter Default = new Filter() {
        @Override
        public Object prefilter(RpcRequest request) {
            return null;
        }

        @Override
        public Object postfilter(RpcRequest request, RpcResponse response, Object result) {
            return null;
        }
    };

}
