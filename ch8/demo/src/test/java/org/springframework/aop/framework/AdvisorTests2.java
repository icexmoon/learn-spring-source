package org.springframework.aop.framework;

import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.*;
import org.springframework.aop.interceptor.ExposeInvocationInterceptor;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName AdvisorTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/26 下午4:23
 * @Version 1.0
 */
public class AdvisorTests2 {
    public static class Hello {
        public void sayHello() {
            System.out.println("Hello");
        }
    }

    @Aspect
    static class MyAspect {
        @Before("execution(* sayHello())")
        public void before() {
            System.out.println("before advisor");
        }

        @AfterReturning("execution(* sayHello())")
        public void after() {
            System.out.println("after advisor");
        }

        @Around("execution(* sayHello())")
        public Object around(ProceedingJoinPoint pjp) {
            System.out.println("around advisor before");
            Object retVal = null;
            try {
                retVal = pjp.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
            System.out.println("around advisor after");
            return retVal;
        }
    }

    /**
     * 解析 Aspect 定义的切面，返回拆分后的 Advisor 切面集合
     *
     * @param clazz Aspect 类
     * @return Advisor 集合
     */
    public static List<Advisor> parseAspect(Class<?> clazz) {
        AspectInstanceFactory aif = new SingletonAspectInstanceFactory(new MyAspect());
        // 检查是否为 @Aspect 定义的切面类
        Aspect aspectAnnotation = clazz.getAnnotation(Aspect.class);
        if (aspectAnnotation == null) {
            throw new RuntimeException("@Aspect annotation not found");
        }
        // 依次解析方法的通知注解，生成对应的 Advisor
        List<Advisor> advisorList = new ArrayList<>();
        for (Method method : clazz.getDeclaredMethods()) {
            Before beforeAnnotation = method.getAnnotation(Before.class);
            AfterReturning afterReturningAnnotation = method.getAnnotation(AfterReturning.class);
            Around aroundAnnotation = method.getAnnotation(Around.class);
            if (beforeAnnotation != null) {
                // 生成前置切面
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(beforeAnnotation.value());
                AspectJMethodBeforeAdvice advice = new AspectJMethodBeforeAdvice(method, pointcut, aif);
                DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                advisorList.add(advisor);
            } else if (afterReturningAnnotation != null) {
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(afterReturningAnnotation.value());
                AspectJAfterReturningAdvice advice = new AspectJAfterReturningAdvice(method, pointcut, aif);
                DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                advisorList.add(advisor);
            } else if (aroundAnnotation != null) {
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(aroundAnnotation.value());
                AspectJAroundAdvice advice = new AspectJAroundAdvice(method, pointcut, aif);
                DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                advisorList.add(advisor);
            } else {
                ;
            }
        }
        return advisorList;
    }

    @Test
    public void test2() throws Throwable {
        List<Advisor> advisorList = parseAspect(MyAspect.class);
        for (Advisor advisor : advisorList) {
            System.out.println(advisor);
        }
        ProxyFactory proxyFactory = new ProxyFactory();
        Hello target = new Hello();
        Method targetMethod = target.getClass().getDeclaredMethod("sayHello");
        proxyFactory.setTarget(target);
        proxyFactory.addAdvisors(advisorList);
        // 将非 MethodInterceptor 类型的通知转换为环绕通知
        List<Object> interceptors = proxyFactory.getInterceptorsAndDynamicInterceptionAdvice(targetMethod, target.getClass());
        for (Object interceptor : interceptors) {
            System.out.println(interceptor);
        }
        Hello proxy = (Hello) proxyFactory.getProxy();
        MethodInvocation methodInvocation = new ReflectiveMethodInvocation(proxy, target, targetMethod, new Object[0], target.getClass(), interceptors);
        // 执行会报错 No MethodInvocation found
        // 需要将 methodInvocation 绑定到线程上下文中
        methodInvocation.proceed();
    }

    @Test
    public void test3() throws Throwable {
        List<Advisor> advisorList = parseAspect(MyAspect.class);
        for (Advisor advisor : advisorList) {
            System.out.println(advisor);
        }
        ProxyFactory proxyFactory = new ProxyFactory();
        Hello target = new Hello();
        Method targetMethod = target.getClass().getDeclaredMethod("sayHello");
        proxyFactory.setTarget(target);
        // 将 Method Invocation 绑定到线程上下文中
        proxyFactory.addAdvice(ExposeInvocationInterceptor.INSTANCE);
        proxyFactory.addAdvisors(advisorList);
        List<Object> interceptors = proxyFactory.getInterceptorsAndDynamicInterceptionAdvice(targetMethod, target.getClass());
        for (Object interceptor : interceptors) {
            System.out.println(interceptor);
        }
        Hello proxy = (Hello) proxyFactory.getProxy();
        MethodInvocation methodInvocation = new ReflectiveMethodInvocation(proxy, target, targetMethod, new Object[0], target.getClass(), interceptors);
        methodInvocation.proceed();
    }
}