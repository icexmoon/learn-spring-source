package cn.icexmoon.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @ClassName $Proxy0
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/24 上午10:24
 * @Version 1.0
 */
class $Proxy0 extends Proxy implements HowToWorkTests6.DoSomething {

    public $Proxy0(InvocationHandler invokeHandler) {
        super(invokeHandler);
    }

    @Override
    public void doSomething() {
        try {
            h.invoke(this, HowToWorkTests6.DoSomething.class.getMethod("doSomething"), null);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    @Override
    public void doSomethingElse() {
        try {
            h.invoke(this, HowToWorkTests6.DoSomething.class.getMethod("doSomethingElse"), null);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    @Override
    public int plus(int a, int b) {
        int result;
        try {
            Object[] args = {a, b};
            result = (int) h.invoke(this, HowToWorkTests6.DoSomething.class.getMethod("plus", int.class, int.class), args);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new UndeclaredThrowableException(e);
        }
        return result;
    }

}
