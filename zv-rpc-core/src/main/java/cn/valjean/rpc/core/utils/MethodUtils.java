package cn.valjean.rpc.core.utils;

import java.lang.reflect.Method;
import java.util.Arrays;

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

    /**
     * 获取方法的自定义签名
     *
     * @param method
     * @return
     */
    public static String methodSign(Method method) {
        // 过滤掉本地方法
        if (checkLocalMethod(method)) {
            return "";
        }

        StringBuffer sign = new StringBuffer(method.getName());
        sign.append("@").append(method.getParameterCount());
        Arrays.stream(method.getParameterTypes())
                .forEach(x -> sign.append("_").append(x.getCanonicalName()));

        return sign.toString();
    }


    public static Method findMethod(Class<?> aclas, String methodName) {
        for (Method method : aclas.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }


}
