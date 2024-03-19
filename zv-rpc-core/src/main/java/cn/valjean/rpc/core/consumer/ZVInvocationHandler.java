package cn.valjean.rpc.core.consumer;

import cn.valjean.rpc.core.api.RpcRequest;
import cn.valjean.rpc.core.api.RpcResponse;
import cn.valjean.rpc.core.utils.MethodUtils;
import cn.valjean.rpc.core.utils.TypeUtils;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import okhttp3.*;

import java.io.IOException;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ZVInvocationHandler implements InvocationHandler {

    final static MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");

    Class<?> service;

    public ZVInvocationHandler(Class<?> service) {
        this.service = service;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (MethodUtils.checkLocalMethod(method.getName())) {
            return null;
        }

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethodSign(method.getName());
        rpcRequest.setArgs(args);

        RpcResponse rpcResponse = post(rpcRequest);
        // success
        if (rpcResponse.isStatus()) {
            Object data = rpcResponse.getData();
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
                    System.out.println(genericReturnType);
                    if (genericReturnType instanceof ParameterizedType parameterizedType) {
                        Type actualType = parameterizedType.getActualTypeArguments()[0];
                        System.out.println(actualType);
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

        } else {
            Exception ex = rpcResponse.getEx();
            throw new RuntimeException(ex);
        }
    }

    OkHttpClient client = new OkHttpClient.Builder()
            .connectionPool(new ConnectionPool(16, 60, TimeUnit.SECONDS))
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
            .build();


    private RpcResponse post(RpcRequest rpcRequest) {
        String reqJson = JSON.toJSONString(rpcRequest);
        System.out.println(" reqJson data" + reqJson);
        // provider api
        Request request = new Request.Builder()
                .url("http://localhost:8080/")
                .post(RequestBody.create(reqJson, JSONTYPE))
                .build();
        try {
            String respJson = client.newCall(request).execute().body().string();
            System.out.println(" ===> respJson = " + respJson);
            RpcResponse rpcResponse = JSON.parseObject(respJson, RpcResponse.class);
            return rpcResponse;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
