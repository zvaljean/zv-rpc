package cn.valjean.rpc.core.registry;

import cn.valjean.rpc.core.api.RegistryCenter;
import cn.valjean.rpc.core.meta.InstanceMeta;
import cn.valjean.rpc.core.meta.ServiceMeta;
import lombok.Data;

import java.util.List;


@Data
public class StaticRegisterCenter implements RegistryCenter {
    List<InstanceMeta> providers;

    public StaticRegisterCenter(List<InstanceMeta> providers) {
        this.providers = providers;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {

    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {

    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        return providers;
    }

    @Override
    public void subscribe(ServiceMeta service, ChangedListener instance) {

    }
}
