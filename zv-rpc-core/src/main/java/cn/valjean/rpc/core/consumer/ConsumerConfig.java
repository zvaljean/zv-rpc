package cn.valjean.rpc.core.consumer;

import cn.valjean.rpc.core.api.LoadBalancer;
import cn.valjean.rpc.core.api.Router;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Router router() {
        return Router.Default;
    }

    @Bean
    public LoadBalancer loadBalancer() {
        return LoadBalancer.DefaultLoadBalancer;
    }

}
