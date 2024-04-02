package cn.valjean.rpc.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

@Slf4j
class MethodUtilsTest {

    @Test
    void methodSign() {
        for (Method method : MethodUtils.class.getMethods()) {
            String s = MethodUtils.methodSign(method);
            if (s.length() == 0)
                continue;
            log.debug("method-sign => " + s);

        }
    }
}
