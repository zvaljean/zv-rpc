package cn.valjean.rpc.core.provider;

import cn.valjean.rpc.core.api.RegistryCenter;
import cn.valjean.rpc.core.registry.ZkRegisterCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

@Configurable
public class ProviderConfig {

    @Bean
    ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }


//    @Bean(initMethod = "start", destroyMethod = "stop")
@Bean
public RegistryCenter registryCenter() {
    return new ZkRegisterCenter();
}


    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerBootstrap_runner(@Autowired ProviderBootstrap providerBootstrap) {

        return x -> {
            System.out.println("providerBootstrap = starting ----------- ");
            providerBootstrap.start();
            System.out.println("providerBootstrap = started ############ ");
        };
    }

}
