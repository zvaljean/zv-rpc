package cn.valjean.rpc.demo.provider;

import cn.valjean.rpc.core.api.RpcRequest;
import cn.valjean.rpc.core.api.RpcResponse;
import cn.valjean.rpc.core.provider.ProviderBootstrap;
import cn.valjean.rpc.core.utils.MethodUtils;
import cn.valjean.rpc.demo.api.UserService;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class RpcDemoProviderApplicationTest {

    @Autowired
    ProviderBootstrap providerBootstrap;

    /**
     * 测试无重载的方法
     */
    @DisplayName("UserService-findById-1")
    @Test
    @Disabled(value = "实现已更改，废弃测试")
    void findById() {
        RpcRequest request = new RpcRequest();
        request.setService("cn.valjean.rpc.demo.api.UserService");
//        request.setMethod("findById");
        Integer[] args = new Integer[]{200};
        request.setArgs(args);
        RpcResponse rpcResponse = providerBootstrap.invoke(request);
        System.out.println("return : " + rpcResponse.getData());
    }

    @Test
    void testMethodSign() {
        List<String> signs = getMethodSign("findPerson");
        signs.forEach(System.out::println);

    }

    List<String> getMethodSign(String name) {
        ArrayList<String> signs = new ArrayList<>();
        for (Method method : UserService.class.getMethods()) {
            if (name.equals(method.getName())) {
                signs.add(MethodUtils.methodSign(method));
            }
        }
        return signs;
    }

    /**
     * 测试重载的方法
     */
    @Tag("Override")
    @DisplayName("UserService-findPerson")
    @Test
    void findByIdOverride() {
        RpcRequest request = new RpcRequest();
        request.setService("cn.valjean.rpc.demo.api.UserService");
//        findPerson@1_java.lang.String
//        findPerson@2_java.lang.String_java.lang.Integer
        request.setMethodSign("findPerson@1_java.lang.String");
        Integer[] args = new Integer[]{200};
        request.setArgs(args);

        RpcResponse rpcResponse = providerBootstrap.invoke(request);
        System.out.println("return : " + rpcResponse.getData());
    }

}