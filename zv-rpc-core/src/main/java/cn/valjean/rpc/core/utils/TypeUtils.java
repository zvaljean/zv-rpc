package cn.valjean.rpc.core.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;

@Slf4j
public class TypeUtils {
    public static Object cast(Object source, Class<?> type) {
        if (source == null)
            return null;
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

    @Nullable
    public static Object caseMethodResult(Method method, Object data) {
        Class<?> returnType = method.getReturnType();
        // issue instanceof new feather
        if (data instanceof JSONObject jsonResult) {
            // return map
            if (Map.class.isAssignableFrom(returnType)) {
                Map map = new HashMap();
                // issue: getGenericReturnType
                Type genericReturnType = method.getGenericReturnType();
                // in and out , both is map ?
                // what's the nature of jsonObject?
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    Class<?> keyType = (Class<?>) parameterizedType.getActualTypeArguments()[0];
                    Class<?> valueType = (Class<?>) parameterizedType.getActualTypeArguments()[1];
                    jsonResult.entrySet().stream().forEach(
                            e -> {
                                Object key = TypeUtils.cast(e.getKey(), keyType);
                                Object value = TypeUtils.cast(e.getValue(), valueType);
                                map.put(key, value);
                            });
                }
                return map;
            }
            return jsonResult.toJavaObject(returnType);
        } else if (data instanceof JSONArray jsonArray) {
            Object[] array = jsonArray.stream().toArray();
            if (returnType.isArray()) {
                // issue: getComponentType
                Class<?> componentType = returnType.getComponentType();
                Object resultArray = Array.newInstance(componentType, array.length);
                for (int i = 0; i < array.length; i++) {
                    // issue
                    if (componentType.isPrimitive() || componentType.getPackageName().startsWith("java")) {
                        Array.set(resultArray, i, array[i]);
                    } else {
                        Object castObject = TypeUtils.cast(array[i], componentType);
                        Array.set(resultArray, i, castObject);
                    }
                }
                return resultArray;
            } else if (List.class.isAssignableFrom(returnType)) {
                List<Object> resultList = new ArrayList<>(array.length);
                Type genericReturnType = method.getGenericReturnType();
                log.debug("{}", genericReturnType);
                if (genericReturnType instanceof ParameterizedType parameterizedType) {
                    Type actualType = parameterizedType.getActualTypeArguments()[0];
                    log.debug("{}", actualType);
                    for (Object o : array) {
                        resultList.add(TypeUtils.cast(o, (Class<?>) actualType));
                    }
                } else {
                    resultList.addAll(Arrays.asList(array));
                }
                return resultList;
            } else {
                return null;
            }
        } else {
            return TypeUtils.cast(data, returnType);
        }
    }

}
