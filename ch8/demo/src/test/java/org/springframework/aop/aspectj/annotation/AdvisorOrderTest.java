package org.springframework.aop.aspectj.annotation;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName AspectTests
 * @Description AOP 如何根据切面创建代理
 * @Author icexmoon@qq.com
 * @Date 2025/6/25 下午8:01
 * @Version 1.0
 */
public class AdvisorOrderTest {
    static class Hello{
        public void sayHello(){
            System.out.println("Hello");
        }
        public void sayGoodBye(){
            System.out.println("Goodbye");
        }
    }

    static class Hello2{
        public void sayGoodBye(){
            System.out.println("Goodbye");
        }
    }

    @Aspect
    @Order(2)
    @Component
    static class MyAspect{
        @Pointcut("execution(* sayHello())")
        public void sayHelloCall(){}

        @Before("sayHelloCall()")
        public void before(){
            System.out.println("aspect before sayHelloCall");
        }

        @After("sayHelloCall()")
        public void after(){
            System.out.println("aspect after sayHelloCall");
        }
    }

    @Configuration
    static class MyConfig{
        @Bean
        public DefaultPointcutAdvisor advisor(){
            DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression("execution(* sayHello())");
            advisor.setPointcut(pointcut);
            advisor.setAdvice(new MethodInterceptor() {
                @Override
                public Object invoke(MethodInvocation invocation) throws Throwable {
                    System.out.println("advisor before advice");
                    Object proceed = invocation.proceed();
                    System.out.println("advisor after advice");
                    return proceed;
                }
            });
            advisor.setOrder(Integer.MAX_VALUE);
            return advisor;
        }
    }

    @Test
    public void test(){
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean(MyAspect.class);
        ctx.registerBean(MyConfig.class);
        ctx.registerBean(ConfigurationClassPostProcessor.class);
        ctx.refresh();
        String[] beanDefinitionNames = ctx.getBeanDefinitionNames();
        for(String beanName : beanDefinitionNames){
            System.out.println(beanName);
        }
        ctx.close();
    }

    @Test
    public void test2(){
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean(MyAspect.class);
        ctx.registerBean(MyConfig.class);
        ctx.registerBean(ConfigurationClassPostProcessor.class);
        ctx.registerBean(AnnotationAwareAspectJAutoProxyCreator.class);
        ctx.registerBean(Hello.class);
        ctx.registerBean(Hello2.class);
        ctx.refresh();
        // 检查类型对应的基本切面有哪些
        AnnotationAwareAspectJAutoProxyCreator autoProxyCreator = ctx.getBean(AnnotationAwareAspectJAutoProxyCreator.class);
        List<Advisor> candidateAdvisors = autoProxyCreator.findCandidateAdvisors();
        for (Advisor candidateAdvisor : candidateAdvisors) {
            System.out.println(candidateAdvisor);
        }
        Hello hello = ctx.getBean(Hello.class);
        hello.sayHello();
        hello.sayGoodBye();
        ctx.close();
    }
}
