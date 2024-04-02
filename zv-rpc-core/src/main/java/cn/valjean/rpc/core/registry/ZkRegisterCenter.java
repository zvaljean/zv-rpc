package cn.valjean.rpc.core.registry;

import cn.valjean.rpc.core.api.RegistryCenter;
import cn.valjean.rpc.core.meta.InstanceMeta;
import cn.valjean.rpc.core.meta.ServiceMeta;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Slf4j
public class ZkRegisterCenter implements RegistryCenter {


    @Value("${zk.url}")
    String zkServer;

    @Value("${zk.namespace}")
    String zkNameSpace;

    CuratorFramework client;

    @Override
    public void start() {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        client = CuratorFrameworkFactory.builder()
                .connectString(zkServer)
                .namespace(zkNameSpace)
                .retryPolicy(retryPolicy)
                .build();

        client.start();
        log.debug("zk--------> start");
    }

    @Override
    public void stop() {
        client.close();
        log.debug("zk--------> stop");
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        String servicePath = "/" + service.toPath();
        try {
            // 创建服务的持久化节点
            if (client.checkExists().forPath(servicePath) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            // 创建实例的临时性节点
            String instancePath = servicePath + "/" + instance.toPath();
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "provider".getBytes());
            log.debug("zk--------> register");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {

        String servicePath = "/" + service.toPath();
        log.debug("servicePath = " + servicePath);
        log.debug("instance = " + instance.toUrl());
        try {
            // 判断服务是否存在
            if (client.checkExists().forPath(servicePath) == null) {
                return;
            }
            // 删除实例节点,
            // 如果有子节点也删除
            client.delete().quietly().deletingChildrenIfNeeded().forPath(servicePath);
            //            client.delete().forPath(servicePath);
            log.debug("zk--------> unregister");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        String servicePath = "/" + service.toPath();
        try {
            //            List<InstanceMeta> nodes =
            List<String> data = client.getChildren().forPath(servicePath);
            return InstanceMeta.convertInstance(data);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribe(ServiceMeta service, ChangedListener instance) {

    }
}
