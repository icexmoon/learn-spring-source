package com.example.demo;

import jakarta.annotation.PostConstruct;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.junit.jupiter.api.Test;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @ClassName ProxyCreateTimeTests
 * @Description 代理创建时机
 * @Author icexmoon@qq.com
 * @Date 2025/6/25 下午8:53
 * @Version 1.0
 */
public class ProxyCreateTimeTests2 {
    static class Bean1{
        private Bean2 bean2;
        @Autowired
        public void setBean2(Bean2 bean2) {
            System.out.println("Bean1 setBean2");
            this.bean2 = bean2;
        }
        public Bean1() {
            System.out.println("Bean1 constructor");
        }

        @PostConstruct
        public void init() {
            System.out.println("Bean1 @PostConstruct");
        }

        public void hello() {
            System.out.println("Bean1 hello");
        }
    }

    static class Bean2{
        public Bean2() {
            System.out.println("Bean2 constructor");
        }

        private Bean1 bean1;

        @Autowired
        public void setBean1(Bean1 bean1) {
            System.out.println("Bean2 setBean1");
            this.bean1 = bean1;
        }

        @PostConstruct
        public void init() {
            System.out.println("Bean2 @PostConstruct");
        }

    }

    @Configuration
    static class Config{
        @Bean
        public Bean1 bean1(){
            return new Bean1();
        }

        @Bean
        public Bean2 bean2(){
            return new Bean2();
        }

        @Bean
        public AnnotationAwareAspectJAutoProxyCreator annotationAwareAspectJAutoProxyCreator(){
            return new AnnotationAwareAspectJAutoProxyCreator();
        }

        @Bean
        public AutowiredAnnotationBeanPostProcessor autowiredAnnotationBeanPostProcessor(){
            return new AutowiredAnnotationBeanPostProcessor();
        }

        @Bean
        public CommonAnnotationBeanPostProcessor commonAnnotationBeanPostProcessor(){
            return new CommonAnnotationBeanPostProcessor();
        }

        @Bean
        public Advisor advisor(){
            DefaultPointcutAdvisor advisor = new DefaultPointcutAdvisor();
            AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
            pointcut.setExpression("execution(* hello(..))");
            advisor.setPointcut(pointcut);
            advisor.setAdvice(new MethodInterceptor() {
                @Override
                public Object invoke(MethodInvocation invocation) throws Throwable {
                    System.out.println("before advice");
                    Object result = invocation.proceed();
                    System.out.println("after advice");
                    return result;
                }
            });
            return advisor;
        }
    }

    @Test
    public void test() {
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean(ConfigurationClassPostProcessor.class);
        ctx.registerBean(Config.class);
        ctx.refresh();
        ctx.close();
    }
}
