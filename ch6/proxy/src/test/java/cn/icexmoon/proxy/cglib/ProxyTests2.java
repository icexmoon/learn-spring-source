package cn.icexmoon.proxy.cglib;

import cn.icexmoon.proxy.Target;
import cn.icexmoon.proxy.cglib.fastclass.MethodInterceptor;
import cn.icexmoon.proxy.cglib.fastclass.MethodProxy;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

/**
 * @ClassName ProxyTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/24 下午2:32
 * @Version 1.0
 */
public class ProxyTests2 {
    @Test
    public void test() {
        Target target = new Target();
        Target proxyInstance = new $Proxy2(new MethodInterceptor() {
            @Override
            public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                System.out.println("before...");
                Object result = method.invoke(target, args);
                System.out.println("after...");
                return result;
            }
        });
        proxyInstance.doSomething();
        proxyInstance.doSomethingElse();
        int plus = proxyInstance.plus(1, 2);
        System.out.println("plus : " + plus);
    }

    @Test
    public void test1() {
        Target target = new Target();
        Target proxyInstance = new $Proxy2(new MethodInterceptor() {
            @Override
            public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                System.out.println("before...");
                Object result = methodProxy.invoke(target, args);
                System.out.println("after...");
                return result;
            }
        });
        proxyInstance.doSomething();
        proxyInstance.doSomethingElse();
        int plus = proxyInstance.plus(1, 2);
        System.out.println("plus : " + plus);
    }

    @Test
    public void test2() {
        Target target = new Target();
        Target proxyInstance = new $Proxy2(new MethodInterceptor() {
            @Override
            public Object intercept(Object proxy, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
                System.out.println("before...");
                Object result = methodProxy.invokeSuper(proxy, args);
                System.out.println("after...");
                return result;
            }
        });
        proxyInstance.doSomething();
        proxyInstance.doSomethingElse();
        int plus = proxyInstance.plus(1, 2);
        System.out.println("plus : " + plus);
    }
}
