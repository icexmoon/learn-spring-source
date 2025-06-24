package cn.icexmoon.proxy;

import org.junit.jupiter.api.Test;

/**
 * @ClassName HowToWorkTests
 * @Description JDK 动态代理如何实现
 * @Author icexmoon@qq.com
 * @Date 2025/6/23 下午7:52
 * @Version 1.0
 */
public class HowToWorkTests2 {
    interface DoSomething{
        void doSomething();
    }
    static class Target implements DoSomething{

        @Override
        public void doSomething() {
            System.out.println("Target doSomething");
        }
    }

    @FunctionalInterface
    interface InvokeHandler{
        void invoke();
    }

    static class $Proxy0 implements DoSomething{
        private InvokeHandler invokeHandler;
        public $Proxy0(InvokeHandler invokeHandler) {
            this.invokeHandler = invokeHandler;
        }

        @Override
        public void doSomething() {
            invokeHandler.invoke();
        }
    }

    @Test
    public void test(){
        Target target = new Target();
        $Proxy0 proxy = new $Proxy0(()->{
            System.out.println("before...");
            target.doSomething();
            System.out.println("after...");
        });
        proxy.doSomething();
    }
}
