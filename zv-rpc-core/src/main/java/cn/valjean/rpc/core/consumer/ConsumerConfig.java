package cn.valjean.rpc.core.consumer;

import cn.valjean.rpc.core.api.LoadBalancer;
import cn.valjean.rpc.core.api.RegistryCenter;
import cn.valjean.rpc.core.api.Router;
import cn.valjean.rpc.core.meta.InstanceMeta;
import cn.valjean.rpc.core.registry.ZkRegisterCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class ConsumerConfig {

    @Bean
    ConsumerBootstrap createConsumerBootstrap() {
        return new ConsumerBootstrap();
    }

    @Value("${zvrpc.providers}")
    String services;

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerBootstrap_runner(@Autowired ConsumerBootstrap consumerBootstrap) {

        return x -> {
            System.out.println("consumerBootstrap = starting ----------- ");
            consumerBootstrap.start();
            System.out.println("consumerBootstrap = started ############ ");
        };
    }

    @Bean
    public Router<InstanceMeta> router() {
        return Router.Default;
    }

    @Bean
    public LoadBalancer<InstanceMeta> loadBalancer() {
        return LoadBalancer.DefaultLoadBalancer;
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter registryCenter() {
//        return new StaticRegisterCenter(List.of(services.split(",")));
        return new ZkRegisterCenter();
    }

}
