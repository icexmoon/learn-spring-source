package cn.icexmoon.demo;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * @ClassName ReflectParamNamesTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/29 下午3:15
 * @Version 1.0
 */
public class ReflectParamNamesTests {
    public static void main(String[] args) throws NoSuchMethodException {
        // 通过反射的方式获取参数名称
        Method method = User.class.getDeclaredMethod("setName", String.class);
        Parameter[] parameters = method.getParameters();
        for (Parameter parameter : parameters) {
            System.out.println(parameter.getName());
        }
    }
}
