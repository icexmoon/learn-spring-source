package cn.icexmoon.proxy;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * @ClassName MethodReflectTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/24 上午11:20
 * @Version 1.0
 */
public class MethodReflectTests {
    static class Hello {
        public static void sayHello() {
            System.out.println("hello");
        }
    }

    @Test
    @SneakyThrows
    public void test() {
        Method method = Hello.class.getDeclaredMethod("sayHello");
        for (int i = 0; i < 40; i++) {
            show(i, method);
            method.invoke(null);
        }
    }

    private static void show(int i, Method method) throws Exception {
        Method getMethodAccessor = Method.class.getDeclaredMethod("getMethodAccessor");
        getMethodAccessor.setAccessible(true);
        Object methodAccessor = getMethodAccessor.invoke(method);
        if (methodAccessor == null) {
            System.out.println(i + ":" + null);
            return;
        }
        System.out.println(i + ":" + methodAccessor.getClass());
//        Field delegate = Class.forName("jdk.internal.reflect.DelegatingMethodAccessorImpl").getDeclaredField("altDelegate");
//        delegate.setAccessible(true);
//        System.out.println(i + ":" + delegate.get(methodAccessor));
//        Method delegate = Class.forName("jdk.internal.reflect.DelegatingMethodAccessorImpl").getDeclaredMethod("delegate");
//        delegate.setAccessible(true);
//        System.out.println(i + ":" + delegate.invoke(methodAccessor));
    }
}
