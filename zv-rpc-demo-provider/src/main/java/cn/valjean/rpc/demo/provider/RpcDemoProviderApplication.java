package cn.valjean.rpc.demo.provider;

import cn.valjean.rpc.core.api.RpcRequest;
import cn.valjean.rpc.core.api.RpcResponse;
import cn.valjean.rpc.core.provider.ProviderBootstrap;
import cn.valjean.rpc.core.provider.ProviderConfig;
import jakarta.annotation.Resource;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
@Import({ProviderConfig.class})
public class RpcDemoProviderApplication {

    @Resource
    ProviderBootstrap providerBootstrap;

    public static void main(String[] args) {
        SpringApplication.run(RpcDemoProviderApplication.class, args);
    }

    // 使用HTTP + JSON 来实现序列化和通信
//    @Autowired
//    ProviderBootstrap providerBootstrap;

    @RequestMapping("/")
    public RpcResponse invoke(@RequestBody RpcRequest request) {
        return providerBootstrap.invoke(request);
    }


}
