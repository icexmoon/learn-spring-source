package cn.icexmoon.proxy.cglib;

import cn.icexmoon.proxy.Target;
import org.springframework.cglib.proxy.MethodInterceptor;

import java.lang.reflect.Method;
import java.lang.reflect.UndeclaredThrowableException;

/**
 * @ClassName $Proxy0
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/24 下午2:22
 * @Version 1.0
 */
public class $Proxy0 extends Target {
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


    public $Proxy0(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    @Override
    public void doSomething() {
        try {
            Object[] args = new Object[0];
            methodInterceptor.intercept(this, METHOD_DO_SOMETHING, args, null);
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
            methodInterceptor.intercept(this, METHOD_DO_SOMETHING_ELSE, args, null);
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
            return (int) methodInterceptor.intercept(this, METHOD_PLUS, args, null);
        } catch (RuntimeException | Error e) {
            throw e;
        } catch (Throwable e) {
            throw new UndeclaredThrowableException(e);
        }
    }
}
