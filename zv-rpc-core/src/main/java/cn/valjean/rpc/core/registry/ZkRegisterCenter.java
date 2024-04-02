package cn.valjean.rpc.core.registry;

import cn.valjean.rpc.core.api.RegistryCenter;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public class ZkRegisterCenter implements RegistryCenter {


    @Value("${zk.url}")
    String zkServer;

    CuratorFramework client;

    @Override
    public void start() {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);

        client = CuratorFrameworkFactory.builder()
                .connectString(zkServer)
                .namespace("valjean")
                .retryPolicy(retryPolicy)
                .build();

        client.start();
        System.out.println("zk--------> start");
    }

    @Override
    public void stop() {
        client.close();
        System.out.println("zk--------> close");
    }

    @Override
    public void register(String service, String instance) {
        String servicePath = "/" + service;
        try {
            // 创建服务的持久化节点
            if (client.checkExists().forPath(servicePath) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            // 创建实例的临时性节点
            String instancePath = servicePath + "/" + instance;
            client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "provider".getBytes());
            System.out.println("zk--------> register");
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public void unregister(String service, String instance) {

        String servicePath = "/" + service;
        System.out.println("servicePath = " + servicePath);
        System.out.println("instance = " + instance);
        try {
            // 判断服务是否存在
            if (client.checkExists().forPath(servicePath) == null) {
                return;
            }
            // 删除实例节点,
            // 如果有子节点也删除
            client.delete().deletingChildrenIfNeeded().forPath(servicePath);
            //            client.delete().forPath(servicePath);
            System.out.println("zk--------> unregister");
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }
    }

    @Override
    public List<String> fetchAll(String service) {
        String servicePath = "/" + service;
        try {
            List<String> nodes = client.getChildren().forPath(servicePath);
            nodes.forEach(System.out::println);
            return nodes;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void subscribe(String service, ChangedListener instance) {

    }
}
