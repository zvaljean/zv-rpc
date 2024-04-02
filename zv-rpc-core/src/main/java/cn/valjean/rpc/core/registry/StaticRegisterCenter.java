package cn.valjean.rpc.core.registry;

import cn.valjean.rpc.core.api.RegistryCenter;
import cn.valjean.rpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.List;


@Data
public class StaticRegisterCenter implements RegistryCenter {
    List<String> providers;

    public StaticRegisterCenter(List<String> providers) {
        this.providers = providers;
    }

    @Override
    public void start() {

    }

    @Override
    public void stop() {

    }

    @Override
    public void register(String service, InstanceMeta instance) {

    }

    @Override
    public void unregister(String service, InstanceMeta instance) {

    }

    @Override
    public List<String> fetchAll(String service) {
        return providers;
    }

    @Override
    public void subscribe(String service, ChangedListener instance) {

    }
}
