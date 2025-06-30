package cn.icexmoon.demo1;

import org.junit.jupiter.api.Test;
import org.springframework.core.GenericTypeResolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @ClassName TypeParameterTests
 * @Description 获取泛型参数
 * @Author icexmoon@qq.com
 * @Date 2025/6/30 下午3:37
 * @Version 1.0
 */
public class TypeParameterTests {
    private static class Parent<T>{}
    private static class Child extends Parent<String>{}

    /**
     * 使用反射获取父类的泛型参数
     */
    @Test
    public void test() {
        Type genericSuperclass = Child.class.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType parameterizedType) {
            Type type = parameterizedType.getActualTypeArguments()[0];
            System.out.println(type);
        }
    }

    /**
     * 通过 Spring API 获取父类的泛型参数
     */
    @Test
    public void test2() {
        Class<?> clazz = GenericTypeResolver.resolveTypeArgument(Child.class, Parent.class);
        System.out.println(clazz);
    }

    @Test
    public void test3() {
        Class<?>[] classes = GenericTypeResolver.resolveTypeArguments(Child.class, Parent.class);
        for (Class<?> aClass : classes) {
            System.out.println(aClass);
        }
    }
}
