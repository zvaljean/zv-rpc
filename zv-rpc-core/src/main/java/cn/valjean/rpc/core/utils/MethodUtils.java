package cn.valjean.rpc.core.utils;

import java.lang.reflect.Method;

public class MethodUtils {

    public static boolean checkLocalMethod(final String methodName) {
        //本地方法不代理
        if ("toString".equals(methodName) ||
                "hashCode".equals(methodName) ||
                "notifyAll".equals(methodName) ||
                "equals".equals(methodName) ||
                "wait".equals(methodName) ||
                "getClass".equals(methodName) ||
                "notify".equals(methodName)) {
            return true;
        }
        return false;
    }

    // 等价上面的
    // issue: 反射相关的内容
    public static boolean checkLocalMethod(final Method method) {
        return method.getDeclaringClass().equals(Object.class);
    }

}
