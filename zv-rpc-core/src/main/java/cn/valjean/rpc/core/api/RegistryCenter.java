package cn.valjean.rpc.core.api;


import cn.valjean.rpc.core.meta.InstanceMeta;
import cn.valjean.rpc.core.registry.ChangedListener;

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
    void register(String service, InstanceMeta instance);

    void unregister(String service, InstanceMeta instance);

    List<InstanceMeta> fetchAll(String service);

    void subscribe(String service, ChangedListener instance);

}
