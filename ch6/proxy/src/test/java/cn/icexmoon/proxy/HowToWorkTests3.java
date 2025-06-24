package cn.icexmoon.proxy;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @ClassName HowToWorkTests
 * @Description JDK 动态代理如何实现
 * @Author icexmoon@qq.com
 * @Date 2025/6/23 下午7:52
 * @Version 1.0
 */
public class HowToWorkTests3 {
    interface DoSomething {
        void doSomething();

        void doSomethingElse();
    }

    static class Target implements DoSomething {

        @Override
        public void doSomething() {
            System.out.println("Target doSomething");
        }

        @Override
        public void doSomethingElse() {
            System.out.println("Target doSomethingElse");
        }
    }

    @FunctionalInterface
    interface InvokeHandler {
        void invoke(Method method, Object[] args) throws Throwable;
    }

    static class $Proxy0 implements DoSomething {
        private final InvokeHandler invokeHandler;

        public $Proxy0(InvokeHandler invokeHandler) {
            this.invokeHandler = invokeHandler;
        }

        @Override
        public void doSomething() {
            try {
                invokeHandler.invoke(DoSomething.class.getMethod("doSomething"), null);
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Throwable e) {
                throw new UndeclaredThrowableException(e);
            }
        }

        @Override
        public void doSomethingElse() {
            try {
                invokeHandler.invoke(DoSomething.class.getMethod("doSomethingElse"), null);
            } catch (RuntimeException | Error e) {
                throw e;
            } catch (Throwable e) {
                throw new UndeclaredThrowableException(e);
            }
        }
    }

    @Test
    public void test() {
        Target target = new Target();
        $Proxy0 proxy = new $Proxy0((method, args) -> {
            System.out.println("before...");
            method.invoke(target, args);
            System.out.println("after...");
        });
        proxy.doSomething();
        proxy.doSomethingElse();
    }
}
