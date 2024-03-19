package cn.valjean.rpc.demo.consumer.srv;

import cn.valjean.rpc.core.annotation.ZVConsumer;
import cn.valjean.rpc.demo.api.UserService;
import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * only for test , inject the user service on test
 */
@Data
@Component
public class SrvConfig {

    @ZVConsumer
    UserService userService;

}
