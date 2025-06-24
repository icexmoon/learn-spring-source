package cn.icexmoon.proxy;

import org.junit.jupiter.api.Test;

/**
 * @ClassName HowToWorkTests
 * @Description JDK 动态代理如何实现
 * @Author icexmoon@qq.com
 * @Date 2025/6/23 下午7:52
 * @Version 1.0
 */
public class HowToWorkTests6 {
    interface DoSomething {
        void doSomething();

        void doSomethingElse();

        int plus(int a, int b);
    }

    @Test
    public void test() {
        Target target = new Target();
        $Proxy0 proxy = new $Proxy0((p, method, args) -> {
            System.out.println("before...");
            Object result = method.invoke(target, args);
            System.out.println("after...");
            return result;
        });
        proxy.doSomething();
        proxy.doSomethingElse();
        int plus = proxy.plus(1, 2);
        System.out.println(plus);
    }
}
