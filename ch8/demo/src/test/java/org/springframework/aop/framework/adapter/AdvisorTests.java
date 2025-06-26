package org.springframework.aop.framework.adapter;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.ProxyMethodInvocation;
import org.springframework.aop.aspectj.*;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName AdvisorTests
 * @Description 展示如何将 Aspect 拆分成 Advisor
 * @Author icexmoon@qq.com
 * @Date 2025/6/26 下午3:30
 * @Version 1.0
 */
public class AdvisorTests {
    public static class Hello {
        public void sayHello() {
            System.out.println("Hello");
        }
    }

    @Aspect
    private static class MyAspect {
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
                pointcut.setExpression(aspectAnnotation.value());
                AspectJMethodBeforeAdvice advice = new AspectJMethodBeforeAdvice(method, pointcut, aif);
                DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                advisorList.add(advisor);
            } else if (afterReturningAnnotation != null) {
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(aspectAnnotation.value());
                AspectJAfterReturningAdvice advice = new AspectJAfterReturningAdvice(method, pointcut, aif);
                DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                advisorList.add(advisor);
            } else if (aroundAnnotation != null) {
                AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
                pointcut.setExpression(aspectAnnotation.value());
                AspectJAroundAdvice advice = new AspectJAroundAdvice(method, pointcut, aif);
                DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, advice);
                advisorList.add(advisor);
            } else {
                ;
            }
        }
        return advisorList;
    }

    /**
     * 获取 Advisor 对应的 MethodInterceptor
     * 如果 Advisor 包含的通知不是 MethodInterceptor 类型，转换
     *
     * @param advisorList Advisor 集合
     * @return MethodInterceptor 集合
     */
    public List<MethodInterceptor> exchange(List<Advisor> advisorList) {
        List<MethodInterceptor> methodInterceptorList = new ArrayList<>();
        MethodBeforeAdviceAdapter methodBeforeAdviceAdapter = new MethodBeforeAdviceAdapter();
        AfterReturningAdviceAdapter afterReturningAdviceAdapter = new AfterReturningAdviceAdapter();
        for (Advisor advisor : advisorList) {
            if (methodBeforeAdviceAdapter.supportsAdvice(advisor.getAdvice())) {
                MethodInterceptor interceptor = methodBeforeAdviceAdapter.getInterceptor(advisor);
                methodInterceptorList.add(interceptor);
            } else if (afterReturningAdviceAdapter.supportsAdvice(advisor.getAdvice())) {
                MethodInterceptor interceptor = afterReturningAdviceAdapter.getInterceptor(advisor);
                methodInterceptorList.add(interceptor);
            } else {
                Advice advice = advisor.getAdvice();
                if (!(advice instanceof MethodInterceptor)) {
                    throw new RuntimeException("advice is not MethodInterceptor");
                }
                methodInterceptorList.add((MethodInterceptor) advice);
            }
        }
        return methodInterceptorList;
    }

    static class MyMethodInvocation implements ProxyMethodInvocation {
        // 代理对象
        private Object proxy;
        // 代理目标对象
        private Object target;
        // 代理目标方法
        private Method method;
        // 实际参数
        private Object[] args;
        // 增强方法的调用链
        private List<MethodInterceptor> methodInterceptorList;
        // 链式调用的游标，标记当前调用位置
        private int cursor = 0;

        public MyMethodInvocation(Object proxy, Object target, Method method, Object[] args, List<MethodInterceptor> methodInterceptorList) {
            this.target = target;
            this.method = method;
            this.args = args;
            this.methodInterceptorList = methodInterceptorList;
            this.proxy = proxy;
        }

        /**
         * 链式调用增强方法，并最终调用被代理对象
         *
         * @return 返回值
         * @throws Exception
         */
        @Override
        public Object proceed() throws Throwable {
            if (methodInterceptorList == null || methodInterceptorList.isEmpty()
                    || cursor >= methodInterceptorList.size()) {
                // 如果没有增强方法调用链或所有增强方法已调用，直接调用原始方法并返回
                return method.invoke(target, args);
            }
            // 调用当前增强方法
            MethodInterceptor methodInterceptor = methodInterceptorList.get(cursor);
            cursor++;
            return methodInterceptor.invoke(this);
        }

        @Override
        public Object getThis() {
            return this;
        }

        @Override
        public AccessibleObject getStaticPart() {
            return method;
        }

        @Override
        public Method getMethod() {
            return method;
        }

        @Override
        public Object[] getArguments() {
            return args;
        }

        @Override
        public Object getProxy() {
            return proxy;
        }

        @Override
        public MethodInvocation invocableClone() {
            return new MyMethodInvocation(proxy, target, method, args, methodInterceptorList);
        }

        @Override
        public MethodInvocation invocableClone(Object... arguments) {
            return new MyMethodInvocation(proxy, target, method, args, methodInterceptorList);
        }

        @Override
        public void setArguments(Object... arguments) {
            this.args = arguments;
        }

        @Override
        public void setUserAttribute(String key, Object value) {

        }

        @Override
        public Object getUserAttribute(String key) {
            return null;
        }
    }

    static class MethodInterceptor1 implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            System.out.println("MethodInterceptor1 before advisor");
            Object result = invocation.proceed();
            System.out.println("MethodInterceptor1 after advisor");
            return result;
        }
    }

    static class MethodInterceptor2 implements MethodInterceptor {

        @Override
        public Object invoke(MethodInvocation invocation) throws Throwable {
            System.out.println("MethodInterceptor2 before advisor");
            Object result = invocation.proceed();
            System.out.println("MethodInterceptor2 after advisor");
            return result;
        }
    }

    @Test
    public void test() throws NoSuchMethodException {
        List<Advisor> advisorList = parseAspect(MyAspect.class);
        for (Advisor advisor : advisorList) {
            System.out.println(advisor);
        }
        List<MethodInterceptor> methodInterceptors = exchange(advisorList);
        System.out.println("methodInterceptors============");
        for (MethodInterceptor methodInterceptor : methodInterceptors) {
            System.out.println(methodInterceptor);
        }
        Hello target = new Hello();
        Method method = target.getClass().getDeclaredMethod("sayHello");
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target);
        Object proxy = proxyFactory.getProxy();
        // 调用代理方法
        MyMethodInvocation methodInvocation = new MyMethodInvocation(proxy, target, method, new Object[0], methodInterceptors);
        try {
            methodInvocation.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void test2() throws NoSuchMethodException {
        Hello target = new Hello();
        Method method = target.getClass().getDeclaredMethod("sayHello");
        ProxyFactory proxyFactory = new ProxyFactory();
        proxyFactory.setTarget(target);
        Object proxy = proxyFactory.getProxy();
        List<MethodInterceptor> methodInterceptors = List.of(new MethodInterceptor1(), new MethodInterceptor2());
        // 调用代理方法
        MyMethodInvocation methodInvocation = new MyMethodInvocation(proxy, target, method, new Object[0], methodInterceptors);
        try {
            methodInvocation.proceed();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }
}
