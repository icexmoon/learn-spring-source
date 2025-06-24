package cn.icexmoon.proxy;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @ClassName CglibProxyTests
 * @Description 用 cglib 实现动态代理
 * @Author icexmoon@qq.com
 * @Date 2025/6/23 下午1:51
 * @Version 1.0
 */
public class CglibProxyTests {
    private static final Logger log = LoggerFactory.getLogger(CglibProxyTests.class);

    @Slf4j
    static class Target{
        public void doSomething(){
            log.info("doSomething");
        }
    }

    @Test
    public void test() {
        Target target = new Target();
        Target proxyInstance = (Target)Enhancer.create(Target.class, new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                log.info("before...");
                Object result = method.invoke(target, args);
                log.info("after...");
                return result;
            }
        });
        proxyInstance.doSomething();
    }

    @Test
    public void test2() {
        Target target = new Target();
        Target proxyInstance = (Target)Enhancer.create(Target.class, new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                log.info("before...");
                Object result = proxy.invoke(target, args);
                log.info("after...");
                return result;
            }
        });
        proxyInstance.doSomething();
    }

    @Test
    public void test3() {
        Target target = new Target();
        target.doSomething();
        Target proxyInstance = (Target)Enhancer.create(Target.class, new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                log.info("before...");
                    Object result = proxy.invokeSuper(obj, args);
                log.info("after...");
                return result;
            }
        });
        proxyInstance.doSomething();
    }
}
