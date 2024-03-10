package cn.valjean.rpc.demo.provider;

import cn.valjean.rpc.core.annotation.ZVProvider;
import cn.valjean.rpc.demo.api.User;
import cn.valjean.rpc.demo.api.UserService;
import org.springframework.stereotype.Component;

@Component
@ZVProvider
public class UserServiceImpl implements UserService {

    @Override
    public User findById(Integer id) {
        return new User(id, "valjean-" + System.currentTimeMillis());
    }
}
