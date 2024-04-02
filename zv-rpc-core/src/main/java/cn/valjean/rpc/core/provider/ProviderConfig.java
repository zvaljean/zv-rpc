package cn.valjean.rpc.core.provider;

import cn.valjean.rpc.core.api.RegistryCenter;
import cn.valjean.rpc.core.registry.ZkRegisterCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

@Configurable
@Slf4j
public class ProviderConfig {

    @Bean
    ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }

    @Bean
    ProviderInvoker providerInvoker(@Autowired ProviderBootstrap providerBootstrap) {
        return new ProviderInvoker(providerBootstrap);
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
            log.debug("providerBootstrap = starting ----------- ");
            providerBootstrap.start();
            log.debug("providerBootstrap = started ############ ");
        };
    }

}
