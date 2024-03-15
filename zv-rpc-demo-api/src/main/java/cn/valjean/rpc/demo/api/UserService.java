package cn.valjean.rpc.demo.api;

public interface UserService {

    User findById(Integer id);

    User findPerson(String name);

    User findPerson(String name, Integer age);
}
