package cn.icexmoon.proxy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @ClassName ProxyTesets
 * @Description 用 jdk 实现动态代理
 * @Author icexmoon@qq.com
 * @Date 2025/6/23 下午1:36
 * @Version 1.0
 */
public class JdkProxyTests {
    private static final Logger log = LoggerFactory.getLogger(JdkProxyTests.class);

    interface DoSomething {
        void doSomething();
    }

    @Slf4j
    static class Target implements DoSomething {

        @Override
        public void doSomething() {
            log.info("doSomething...");
        }
    }

    @Test
    public void testProxy() {
        Target target = new Target();
        DoSomething proxyInstance = (DoSomething)Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{DoSomething.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        log.info("before invoke");
                        Object result = method.invoke(target, args);
                        log.info("after invoke");
                        return result;
                    }
                });
        proxyInstance.doSomething();
    }

    @Test
    public void testProxy2() throws IOException {
        Target target = new Target();
        DoSomething proxyInstance = (DoSomething)Proxy.newProxyInstance(
                this.getClass().getClassLoader(),
                new Class[]{DoSomething.class},
                new InvocationHandler() {
                    @Override
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        log.info("before invoke");
                        Object result = method.invoke(target, args);
                        log.info("after invoke");
                        return result;
                    }
                });
        proxyInstance.doSomething();
        System.out.println(proxyInstance.getClass());
        System.in.read();
    }
}
