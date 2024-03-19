package cn.valjean.rpc.core.api;


import java.util.List;

public interface RegistryCenter {

    /**
     * provider/ consumer
     */
    void start();

    /**
     * provider/ consumer
     */
    void stop();

    /**
     * providers
     *
     * @param service
     * @param instance
     */
    void register(String service, String instance);

    void unregister(String service, String instance);

    List<String> fetchAll(String service);

//    void subscribe(String service, ChangedListener instance);

}
