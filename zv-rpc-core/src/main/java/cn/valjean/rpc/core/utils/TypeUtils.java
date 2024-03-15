package cn.valjean.rpc.core.utils;

import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.List;

public class TypeUtils {
    public static Object cast(Object source, Class<?> type) {
        if (source == null) return null;
        Class<?> aClass = source.getClass();
        // issue
        if (type.isAssignableFrom(aClass)) {
            return source;
        }

        if (type.isArray()) {
            if (source instanceof List lt) {
                source = lt.toArray();
            }
            int len = Array.getLength(source);
            // issue :getComponentType
            Class<?> componentType = type.getComponentType();
            // issue :array utils usage
            Object result = Array.newInstance(componentType, len);
            for (int i = 0; i < len; i++) {
                Array.set(result, i, Array.get(source, i));
            }
            return result;
        }

        if (source instanceof HashMap mp) {
            JSONObject jsonObject = new JSONObject(mp);
            return jsonObject.toJavaObject(type);
        }


        // 基本类型 + 包装类型
        if (type.equals(Integer.class) || type.equals(Integer.TYPE)) {
            return Integer.valueOf(source.toString());
        } else if (type.equals(Long.class) || type.equals(Long.TYPE)) {
            return Long.valueOf(source.toString());
        } else if (type.equals(Float.class) || type.equals(Float.TYPE)) {
            return Float.valueOf(source.toString());
        } else if (type.equals(Double.class) || type.equals(Double.TYPE)) {
            return Double.valueOf(source.toString());
        } else if (type.equals(Byte.class) || type.equals(Byte.TYPE)) {
            return Byte.valueOf(source.toString());
        } else if (type.equals(Short.class) || type.equals(Short.TYPE)) {
            return Short.valueOf(source.toString());
        } else if (type.equals(Character.class) || type.equals(Character.TYPE)) {
            return Character.valueOf(source.toString().charAt(0));
        }


        return null;
    }
}
