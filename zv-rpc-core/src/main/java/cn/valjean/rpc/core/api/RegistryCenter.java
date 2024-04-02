package cn.valjean.rpc.core.api;


import cn.valjean.rpc.core.meta.InstanceMeta;
import cn.valjean.rpc.core.meta.ServiceMeta;
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
    void register(ServiceMeta service, InstanceMeta instance);

    void unregister(ServiceMeta service, InstanceMeta instance);

    List<InstanceMeta> fetchAll(ServiceMeta service);

    void subscribe(ServiceMeta service, ChangedListener instance);

}
