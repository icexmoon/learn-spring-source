package cn.icexmoon.proxy;

import org.junit.jupiter.api.Test;

/**
 * @ClassName HowToWorkTests
 * @Description JDK 动态代理如何实现
 * @Author icexmoon@qq.com
 * @Date 2025/6/23 下午7:52
 * @Version 1.0
 */
public class HowToWorkTests {
    interface DoSomething{
        void doSomething();
    }
    static class Target implements DoSomething{

        @Override
        public void doSomething() {
            System.out.println("Target doSomething");
        }
    }

    static class $Proxy0 implements DoSomething{
        private DoSomething target;
        public $Proxy0(DoSomething target) {
            this.target = target;
        }

        @Override
        public void doSomething() {
            System.out.println("before...");
            target.doSomething();
            System.out.println("after...");
        }
    }

    @Test
    public void test(){
        Target target = new Target();
        $Proxy0 proxy = new $Proxy0(target);
        proxy.doSomething();
    }
}
