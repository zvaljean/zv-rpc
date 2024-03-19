package cn.valjean.rpc.core.api;

import lombok.Data;

import java.util.List;

@Data
public class RpcContext {
    List<Filter> filters;
    Router router;
    LoadBalancer loadBalancer;
}
