package cn.icexmoon.proxy;

import org.junit.jupiter.api.Test;

import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @ClassName AsmWriterTests
 * @Description 利用 ASM 框架生成代理类的字节码
 * @Author icexmoon@qq.com
 * @Date 2025/6/24 上午10:36
 * @Version 1.0
 */
public class AsmWriterTests {
    /**
     * 生成字节码文件
     *
     * @throws Exception
     */
    @Test
    public void test() throws Exception {
        // 生成代理类的字节码
        byte[] dump = $Proxy0Dump.dump();
        // 将字节码写入文件
        String filePath = "D:\\workspace\\learn-spring-source\\ch6\\proxy\\target\\$Proxy0.class";
        FileOutputStream fileOutputStream = new FileOutputStream(filePath);
        fileOutputStream.write(dump);
        fileOutputStream.close();
    }

    @Test
    public void test2() throws Exception {
        byte[] dump = $Proxy0Dump.dump();
        // 创建类加载器
        final String CLASS_NAME = "cn.icexmoon.proxy.$Proxy0";
        ClassLoader classLoader = new ClassLoader() {
            @Override
            protected Class<?> findClass(String name) throws ClassNotFoundException {
                if (name.equals(CLASS_NAME)) {
                    return super.defineClass(CLASS_NAME, dump, 0, dump.length);
                }
                return super.findClass(name);
            }
        };
        // 利用反射创建代理对象
        Class<?> cls = classLoader.loadClass(CLASS_NAME);
        Constructor<?> constructor = cls.getConstructor(InvocationHandler.class);
        HowToWorkTests6.DoSomething proxyInstance = (HowToWorkTests6.DoSomething)constructor.newInstance(new InvocationHandler() {
            @Override
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                Target target = new Target();
                System.out.println("before invoke");
                Object result = method.invoke(target, args);
                System.out.println("after invoke");
                return result;
            }
        });
        proxyInstance.doSomething();
        proxyInstance.doSomethingElse();
        int plus = proxyInstance.plus(1, 2);
        System.out.println(plus);
    }
}
