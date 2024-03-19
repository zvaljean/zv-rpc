package cn.valjean.rpc.demo.consumer;

import cn.valjean.rpc.core.consumer.ConsumerConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
/**
 * issue
 * 通过import加载初始化的各种配置，
 * 完成consumer的准备工作
 */
@Import({ConsumerConfig.class})
public class RpcDemoConsumerApplication {
    public static void main(String[] args) {
        SpringApplication.run(RpcDemoConsumerApplication.class, args);
    }

    /**
     * issue
     * 注解的使用方式
     * 这个 UserService, 被使用代理类来增强功能
     */
//    @ZVConsumer
//    UserService userService;

    /**
     * issue
     * 启动后调用该回调函数。
     * ApplicationRunner 是一个函数式接口，FunctionalInterface
     * 故可以使用lambda来标识
     *
     * @return
     */
//    @Bean
//    public ApplicationRunner consumer_runner() {
//        return x -> {
//            User byId = userService.findById(1);
//            System.out.println("byId = " + byId);
//        };
//    }
}
