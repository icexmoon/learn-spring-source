package com.example.demo;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

/**
 * @ClassName ProxyTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/7/12 下午2:16
 * @Version 1.0
 */
@Slf4j
public class ProxyTests3 {
    @Component
    @Slf4j
    @Getter
    public static class MyBean {
        public void function1(){
            log.info("function1");
        }

        public static void function2(){
            log.info("function2");
        }

        final public void function3(){
            log.info("function3");
        }

        private void function4(){
            log.info("function4");
        }

        private MyBean2 myBean2;
        private boolean initialized = false;

        @Autowired
        public void setMyBean2(MyBean2 myBean2) {
            log.info("MyBean.setMyBean2() is called.");
            this.myBean2 = myBean2;
        }

        public void hello() {
            log.info("MyBean.hello() is called.");
        }

        @PostConstruct
        public void init() {
            this.initialized = true;
            log.info("MyBean.init() is called.");
        }
    }

    @Component
    public static class MyBean2 {
    }

    @Aspect
    @Slf4j
    @Component
    public static class MyAspect {
        @Before("execution(* com.example..MyBean.*(..))")
        public void before() {
            log.info("MyAspect.before() is called.");
        }
    }

    @Configuration
    @EnableAspectJAutoProxy(proxyTargetClass = true)
    @Import({MyAspect.class, MyBean.class, MyBean2.class})
    public static class Config {
    }

    @Test
    public void testProxy() {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
        MyBean beanProxy = ctx.getBean(MyBean.class);
        beanProxy.function1();
        beanProxy.function2();
        beanProxy.function3();
        beanProxy.function4();
    }
}
