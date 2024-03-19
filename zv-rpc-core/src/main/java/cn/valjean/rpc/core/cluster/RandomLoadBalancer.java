package cn.valjean.rpc.core.cluster;

import cn.valjean.rpc.core.api.LoadBalancer;

import java.util.List;

public class RandomLoadBalancer implements LoadBalancer {
    @Override
    public Object choose(List providers) {
        return null;
    }
}
