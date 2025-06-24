package cn.icexmoon.proxy;

/**
 * @ClassName Target
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/24 上午10:58
 * @Version 1.0
 */
public class Target implements HowToWorkTests6.DoSomething {

    @Override
    public void doSomething() {
        System.out.println("Target doSomething");
    }

    @Override
    public void doSomethingElse() {
        System.out.println("Target doSomethingElse");
    }

    @Override
    public int plus(int a, int b) {
        System.out.println("doSomething plus " + a + " and " + b);
        return a + b;
    }
}
