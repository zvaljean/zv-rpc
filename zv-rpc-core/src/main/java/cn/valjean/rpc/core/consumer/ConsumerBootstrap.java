package cn.valjean.rpc.core.consumer;

import cn.valjean.rpc.core.annotation.ZVConsumer;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ConsumerBootstrap implements ApplicationContextAware {

    ApplicationContext applicationContext;

    private Map<String, Object> stub = new HashMap<>();

    public void start() {
        String[] names = applicationContext.getBeanDefinitionNames();
        long begin = System.currentTimeMillis();
        System.out.println("begin => " + begin);

        // 优化点1：过滤去掉spring/jdk/其他框架本身的bean的反射扫描 TODO 1
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            String packageName = bean.getClass().getPackageName();
            if (packageName.startsWith("org.springframework") ||
                    packageName.startsWith("java.") ||
                    packageName.startsWith("javax.") ||
                    packageName.startsWith("jdk.") ||
                    packageName.startsWith("com.fasterxml.") ||
                    packageName.startsWith("com.sun.") ||
                    packageName.startsWith("jakarta.") ||
                    packageName.startsWith("org.apache")) {
                continue;  // 这段逻辑可以降低一半启动速度 300ms->160ms
            }
            System.out.println(packageName + " package bean => " + name);

            List<Field> fields = findAnnotatedField(bean.getClass());
            for (Field field : fields) {
                System.out.println("field name = " + field.getName());
                try {
                    // 此处获得是消费者，也就是service
                    Class<?> service = field.getType();
//                canonical :: 标准
                    String canonicalName = service.getCanonicalName();
                    Object consumer = stub.get(canonicalName);
                    if (consumer == null) {
                        // 没有才创建
                        consumer = createConsumer(service);
                        //将创建好的bean放入其中
                        stub.put(canonicalName, consumer);
                    }
                    field.setAccessible(true);
                    field.set(bean, consumer);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }


        }
    }

    /**
     * 创建相关消费者
     * issue 反射相关内容
     *
     * @param service
     * @return
     */
    private Object createConsumer(Class<?> service) {
        return Proxy.newProxyInstance(service.getClassLoader(),
                // 使用代理来创建consumer，并增强其内容
                new Class[]{service}, new ZVInvocationHandler(service));
    }

    /**
     * 在该类以及其父类里，找到标注有 ZVConsumer 的字段。
     *
     * @param aClass
     * @return
     */
    private List<Field> findAnnotatedField(Class<?> aClass) {
        List<Field> results = new ArrayList<>();
        while (aClass != null) {
            //issue 相关内容点补全
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(ZVConsumer.class)) {
                    results.add(field);
                }
            }
            //fixme 此处还获取父类相关的字段？
            aClass = aClass.getSuperclass();
        }

        return results;
    }
}
