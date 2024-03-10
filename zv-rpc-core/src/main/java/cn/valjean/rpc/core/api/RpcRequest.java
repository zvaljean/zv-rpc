package cn.valjean.rpc.core.api;

import lombok.Data;

@Data
public class RpcRequest {
    private String service;
    private String method;
    private Object[] args;

}
