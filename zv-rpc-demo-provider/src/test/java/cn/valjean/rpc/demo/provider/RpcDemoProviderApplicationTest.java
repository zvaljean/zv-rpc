package cn.valjean.rpc.demo.provider;

import cn.valjean.rpc.core.api.RpcRequest;
import cn.valjean.rpc.core.api.RpcResponse;
import cn.valjean.rpc.core.provider.ProviderBootstrap;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class RpcDemoProviderApplicationTest {

    @Autowired
    ProviderBootstrap providerBootstrap;

    @DisplayName("UserService-findById-1")
    @Test
    void findById() {
        RpcRequest request = new RpcRequest();
        request.setService("cn.valjean.rpc.demo.api.UserService");
        request.setMethod("findById");
        Integer[] args = new Integer[]{200};
        request.setArgs(args);

        RpcResponse rpcResponse = providerBootstrap.invoke(request);
        System.out.println("return : " + rpcResponse.getData());
    }


}