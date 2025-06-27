package org.springframework.aop.aspectj.annotation;

import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.framework.ReflectiveMethodInvocation;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @ClassName DynamicAdviceTests
 * @Description 动态通知
 * @Author icexmoon@qq.com
 * @Date 2025/6/27 上午9:55
 * @Version 1.0
 */
public class DynamicAdviceTests {
    static class Hello {
        public void sayHello(String name) {
            System.out.println("Hello " + name);
        }
    }

    @Aspect
    static class HelloAspect {
        @Before("execution(* sayHello(..))")
        public void before() {
            System.out.println("Before execution");
        }

        @Before("execution(* sayHello(..)) && args(name,..)")
        public void before(String name) {
            System.out.println("Before " + name);
        }
    }

    @Test
    public void testDynamicAdvice() throws Throwable {
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean(Hello.class);
        ctx.registerBean(HelloAspect.class);
        ctx.registerBean(AnnotationAwareAspectJAutoProxyCreator.class);
        ctx.refresh();
        AnnotationAwareAspectJAutoProxyCreator autoProxyCreator = ctx.getBean(AnnotationAwareAspectJAutoProxyCreator.class);
        List<Advisor> candidateAdvisors = autoProxyCreator.findCandidateAdvisors();
        ProxyFactory proxyFactory = new ProxyFactory();
        Hello target = new Hello();
        proxyFactory.setTarget(target);
        proxyFactory.addAdvice(ExposeInvocationInterceptor.INSTANCE);
        proxyFactory.addAdvisors(candidateAdvisors);
        Object proxy = proxyFactory.getProxy();
        Method targetMethod = target.getClass().getDeclaredMethod("sayHello", String.class);
        List<Object> methodInterceptors = proxyFactory.getInterceptorsAndDynamicInterceptionAdvice(targetMethod, target.getClass());
        for (Object methodInterceptor : methodInterceptors) {
            showDetail(methodInterceptor);
        }
        MethodInvocation methodInvocation = new ReflectiveMethodInvocation(proxy, target, targetMethod, new Object[]{"Tom"},
                target.getClass(), methodInterceptors) {
        };
        methodInvocation.proceed();
        ctx.close();
    }


    public static void showDetail(Object o) {
        try {
            Class<?> clazz = Class.forName("org.springframework.aop.framework.InterceptorAndDynamicMethodMatcher");
            if (clazz.isInstance(o)) {
                Field methodMatcher = clazz.getDeclaredField("matcher");
                methodMatcher.setAccessible(true);
                Field methodInterceptor = clazz.getDeclaredField("interceptor");
                methodInterceptor.setAccessible(true);
                System.out.println("环绕通知和切点：" + o);
                System.out.println("\t切点为：" + methodMatcher.get(o));
                System.out.println("\t通知为：" + methodInterceptor.get(o));
            } else {
                System.out.println("普通环绕通知：" + o);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
