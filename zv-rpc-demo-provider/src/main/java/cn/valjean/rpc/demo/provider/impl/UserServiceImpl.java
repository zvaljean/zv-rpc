package cn.valjean.rpc.demo.provider.impl;

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

    /**
     * 按名场抄查找
     *
     * @param name
     * @return
     */
    @Override
    public User findPerson(String name) {
        User user = new User();
        user.setName("one: --> " + name);
        return user;
    }

    /**
     * 按姓名+Id
     *
     * @param name
     * @param id
     * @return
     */
    @Override
    public User findPerson(String name, Integer id) {
        User user = new User();
        user.setName("two: --> " + name);
        user.setId(id);
        return user;
    }

}
