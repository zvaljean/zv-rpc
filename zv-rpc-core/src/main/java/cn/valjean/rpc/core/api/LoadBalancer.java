package cn.valjean.rpc.core.api;

import java.util.List;

/**
 * 挑选同种服务的
 */
//@FunctionalInterface
public interface LoadBalancer<T> {

    T choose(List<T> providers);

//    T choose2(List<T> providers);

//    class DefaultLoadBalancer implements LoadBalancer {
//        @Override
//        public Object choose(List providers) {
//            return (providers == null || providers.size() == 0) ? null : providers.get(0);
//        }
//
//    }

    LoadBalancer DefaultLoadBalancer = providers -> (providers == null || providers.size() == 0) ? null : providers.get(0);


}
