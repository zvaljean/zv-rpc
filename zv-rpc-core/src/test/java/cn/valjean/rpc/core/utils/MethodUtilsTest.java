package cn.valjean.rpc.core.utils;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

class MethodUtilsTest {

    @Test
    void methodSign() {
        for (Method method : MethodUtils.class.getMethods()) {
            String s = MethodUtils.methodSign(method);
            if (s.length() == 0)
                continue;
            System.out.println("method-sign => " + s);

        }
    }
}