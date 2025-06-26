package com.example.demo;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.DefaultPointcutAdvisor;

/**
 * @ClassName AopTests
 * @Description 实现 AOP
 * @Author icexmoon@qq.com
 * @Date 2025/6/24 下午7:17
 * @Version 1.0
 */
public class AopTests {
    interface Hello{
        void sayHello();
        void sayBye();
    }
    static class Target implements Hello{

        @Override
        public void sayHello() {
            System.out.println("Hello World");
        }

        @Override
        public void sayBye() {
            System.out.println("Bye bye");
        }
    }

    static class Target2{

        public void sayHello() {
            System.out.println("Hello World");
        }

        public void sayBye() {
            System.out.println("Bye bye");
        }
    }

    @Test
    public void test() {
        // 切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* sayHello())");
        // 通知
        MethodInterceptor methodInterceptor = new MethodInterceptor() {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                System.out.println("before invoke ");
                Object proceed = invocation.proceed();
                System.out.println("after invoke ");
                return proceed;
            }
        };
        // 切面
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, methodInterceptor);
        // 代理
        ProxyFactory proxyFactory = new ProxyFactory();
        Target target = new Target();
        proxyFactory.setTarget(target);
        proxyFactory.addAdvisor(advisor);
        Hello hello = (Hello) proxyFactory.getProxy();
        System.out.println(hello.getClass());
        hello.sayHello();
        hello.sayBye();
    }

    @Test
    public void test2() {
        // 切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* sayHello())");
        // 通知
        MethodInterceptor methodInterceptor = new MethodInterceptor() {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                System.out.println("before invoke ");
                Object proceed = invocation.proceed();
                System.out.println("after invoke ");
                return proceed;
            }
        };
        // 切面
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, methodInterceptor);
        // 代理
        ProxyFactory proxyFactory = new ProxyFactory();
        Target target = new Target();
        proxyFactory.setProxyTargetClass(false);
        proxyFactory.setInterfaces(target.getClass().getInterfaces());
        proxyFactory.setTarget(target);
        proxyFactory.addAdvisor(advisor);
        Hello hello = (Hello) proxyFactory.getProxy();
        System.out.println(hello.getClass());
        hello.sayHello();
        hello.sayBye();
    }

    @Test
    public void test3() {
        // 切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* sayHello())");
        // 通知
        MethodInterceptor methodInterceptor = new MethodInterceptor() {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                System.out.println("before invoke ");
                Object proceed = invocation.proceed();
                System.out.println("after invoke ");
                return proceed;
            }
        };
        // 切面
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, methodInterceptor);
        // 代理
        ProxyFactory proxyFactory = new ProxyFactory();
        Target target = new Target();
        proxyFactory.setProxyTargetClass(false);
        proxyFactory.setTarget(target);
        proxyFactory.addAdvisor(advisor);
        Hello hello = (Hello) proxyFactory.getProxy();
        System.out.println(hello.getClass());
        hello.sayHello();
        hello.sayBye();
    }

    @Test
    public void test4() {
        // 切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* sayHello())");
        // 通知
        MethodInterceptor methodInterceptor = new MethodInterceptor() {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                System.out.println("before invoke ");
                Object proceed = invocation.proceed();
                System.out.println("after invoke ");
                return proceed;
            }
        };
        // 切面
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, methodInterceptor);
        // 代理
        ProxyFactory proxyFactory = new ProxyFactory();
        Target target = new Target();
        proxyFactory.setProxyTargetClass(true);
        proxyFactory.setTarget(target);
        proxyFactory.addAdvisor(advisor);
        Hello hello = (Hello) proxyFactory.getProxy();
        System.out.println(hello.getClass());
        hello.sayHello();
        hello.sayBye();
    }

    @Test
    public void test5() {
        // 切点
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* sayHello())");
        // 通知
        MethodInterceptor methodInterceptor = new MethodInterceptor() {
            @Override
            public Object invoke(MethodInvocation invocation) throws Throwable {
                System.out.println("before invoke ");
                Object proceed = invocation.proceed();
                System.out.println("after invoke ");
                return proceed;
            }
        };
        // 切面
        DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor(pointcut, methodInterceptor);
        // 代理
        ProxyFactory proxyFactory = new ProxyFactory();
        Target2 target = new Target2();
        proxyFactory.setProxyTargetClass(false);
        proxyFactory.setTarget(target);
        proxyFactory.addAdvisor(advisor);
        Target2 hello = (Target2) proxyFactory.getProxy();
        System.out.println(hello.getClass());
        hello.sayHello();
        hello.sayBye();
    }
}
