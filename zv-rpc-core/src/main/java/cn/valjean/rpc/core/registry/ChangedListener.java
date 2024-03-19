package cn.valjean.rpc.core.registry;

public interface ChangedListener {
    void fire(Event event);
}
