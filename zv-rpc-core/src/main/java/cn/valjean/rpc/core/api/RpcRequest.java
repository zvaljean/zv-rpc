package cn.valjean.rpc.core.api;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RpcRequest {
    private String service;
    // old version
//    private String method;
    private String methodSign;
    private Object[] args;

}
