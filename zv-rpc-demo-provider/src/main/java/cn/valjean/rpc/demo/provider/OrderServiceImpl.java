package cn.valjean.rpc.demo.provider;

import cn.valjean.rpc.core.annotation.ZVProvider;
import cn.valjean.rpc.demo.api.Order;
import cn.valjean.rpc.demo.api.OrderService;
import org.springframework.stereotype.Component;

@ZVProvider
@Component
public class OrderServiceImpl implements OrderService {
    @Override
    public Order findById(Integer id) {
        return new Order(id.longValue(), 33.33f);
    }
}
