package cn.valjean.rpc.demo.provider;

import cn.valjean.rpc.core.api.RpcRequest;
import cn.valjean.rpc.core.api.RpcResponse;
import cn.valjean.rpc.core.provider.ProviderBootstrap;
import cn.valjean.rpc.core.provider.ProviderInvoker;
import cn.valjean.rpc.core.utils.MethodUtils;
import cn.valjean.rpc.demo.api.UserService;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

@SpringBootTest
class RpcDemoProviderApplicationTest {

    @Autowired
    ProviderBootstrap providerBootstrap;

    @Resource
    ProviderInvoker providerInvoker;

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
        RpcResponse rpcResponse = providerInvoker.invoke(request);
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

    private static Stream<Arguments> provideFindPerson() {
        return Stream.of(
                /**
                 * 测试重载方法-1
                 *  {@link cn.valjean.rpc.demo.provider.impl.UserServiceImpl#findPerson(java.lang.String) }
                 */
                Arguments.of("cn.valjean.rpc.demo.api.UserService",
                        "findPerson@1_java.lang.String",
                        new String[]{"valjean"}),

                /**
                 * 测试重载方法-2
                 *  {@link cn.valjean.rpc.demo.provider.impl.UserServiceImpl#findPerson(java.lang.String, java.lang.Integer) }
                 */
                Arguments.of("cn.valjean.rpc.demo.api.UserService",
                        "findPerson@2_java.lang.String_java.lang.Integer",
                        new Object[]{"valjean", 20}));
    }

    /**
     * 测试重载的方法
     * https://www.baeldung.com/parameterized-tests-junit-5
     */
    @Tag("Override")
    @DisplayName("UserService-findPerson")
    @ParameterizedTest
    @MethodSource(value = "provideFindPerson")
    void findByIdOverride(String service, String methodSign, Object[] args) {
        RpcRequest request = new RpcRequest();
        request.setService(service);
        request.setMethodSign(methodSign);
        request.setArgs(args);

        RpcResponse rpcResponse = providerInvoker.invoke(request);
        System.out.println("return : " + rpcResponse.getData());
    }

}
