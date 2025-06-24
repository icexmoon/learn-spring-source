package cn.icexmoon.proxy.cglib.fastclass;


/**
 * @ClassName MethodInterceptor
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/24 下午5:10
 * @Version 1.0
 */
public interface MethodInterceptor {
    Object intercept(Object obj, java.lang.reflect.Method method, Object[] args,
                     MethodProxy proxy) throws Throwable;
}
