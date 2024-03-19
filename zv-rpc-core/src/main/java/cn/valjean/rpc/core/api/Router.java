package cn.valjean.rpc.core.api;

import java.util.List;

/**
 * 集群中流量路由
 */
@FunctionalInterface
public interface Router<T> {
    List<T> route(List<T> routes);

    Router Default = R -> R;
}
