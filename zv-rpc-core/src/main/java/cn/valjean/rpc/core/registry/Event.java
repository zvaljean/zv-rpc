package cn.valjean.rpc.core.registry;

import cn.valjean.rpc.core.meta.InstanceMeta;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Event {
    List<InstanceMeta> data;
}
