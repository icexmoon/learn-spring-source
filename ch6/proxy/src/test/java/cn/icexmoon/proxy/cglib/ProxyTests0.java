package cn.icexmoon.proxy.cglib;

import cn.icexmoon.proxy.Target;
import org.junit.jupiter.api.Test;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @ClassName ProxyTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/24 下午2:32
 * @Version 1.0
 */
public class ProxyTests0 {
    @Test
    public void test() {
        Target target = new Target();
        Target proxyInstance = (Target)new $Proxy0(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
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
}
