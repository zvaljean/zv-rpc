package cn.valjean.rpc.demo.consumer;

import cn.valjean.rpc.core.consumer.ConsumerConfig;
import cn.valjean.rpc.demo.api.User;
import cn.valjean.rpc.demo.consumer.srv.SrvConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;


@Slf4j
@SpringBootTest
@Import({ConsumerConfig.class})
class RpcDemoConsumerApplicationTest {

    @Autowired
    SrvConfig srvConfig;


    @Test
    void consumer_findPerson() {
        User valjean = srvConfig.getUserService().findPerson("valjean");
        log.debug("valjean = " + valjean);
    }

}
