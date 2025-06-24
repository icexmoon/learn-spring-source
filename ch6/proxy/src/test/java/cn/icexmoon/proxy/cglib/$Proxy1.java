package cn.icexmoon.proxy.cglib;

import cn.icexmoon.proxy.Target;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @ClassName $Proxy0
 * @Description 展示 MethodProxy 的用途
 * @Author icexmoon@qq.com
 * @Date 2025/6/24 下午2:22
 * @Version 1.0
 */
public class $Proxy1 extends Target {
    private final MethodInterceptor methodInterceptor;
    private static final Method METHOD_DO_SOMETHING;
    private static final Method METHOD_DO_SOMETHING_ELSE;
    private static final Method METHOD_PLUS;

    static {
        try {
            METHOD_DO_SOMETHING = Target.class.getDeclaredMethod("doSomething");
            METHOD_DO_SOMETHING_ELSE = Target.class.getDeclaredMethod("doSomethingElse");
            METHOD_PLUS = Target.class.getDeclaredMethod("plus", int.class, int.class);
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError(e.getMessage());
        }
    }


    public $Proxy1(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    // 利用 super 构建方法的原始调用
    public void doSomethingSuper(){
        super.doSomething();
    }

    public void doSomethingElseSuper(){
        super.doSomethingElse();
    }

    public int plusSuper(int a, int b){
        return super.plus(a, b);
    }

    // 声明 MethodProxy
    private static MethodProxy METHOD_DO_SOMETHING_PROXY;
    private static MethodProxy METHOD_DO_SOMETHING_ELSE_PROXY;
    private static MethodProxy METHOD_PLUS_PROXY;
    // 初始化
    static {
        METHOD_DO_SOMETHING_PROXY = MethodProxy.create(Target.class, $Proxy1.class, "()V", "doSomething","doSomethingSuper");
        METHOD_DO_SOMETHING_ELSE_PROXY = MethodProxy.create(Target.class, $Proxy1.class, "()V", "doSomethingElse", "doSomethingElseSuper");
        METHOD_PLUS_PROXY = MethodProxy.create(Target.class, $Proxy1.class, "(II)I", "plus", "plusSuper");
    }

    @Override
    public void doSomething() {
        try {
            Object[] args = new Object[0];
            methodInterceptor.intercept(this, METHOD_DO_SOMETHING, args, METHOD_DO_SOMETHING_PROXY);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    @Override
    public void doSomethingElse() {
        try {
            Object[] args = new Object[0];
            methodInterceptor.intercept(this, METHOD_DO_SOMETHING_ELSE, args, METHOD_DO_SOMETHING_ELSE_PROXY);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new UndeclaredThrowableException(e);
        }
    }

    @Override
    public int plus(int a, int b) {
        try {
            Object[] args = {a, b};
            return (int) methodInterceptor.intercept(this, METHOD_PLUS, args, METHOD_PLUS_PROXY);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new UndeclaredThrowableException(e);
        }
    }
}
