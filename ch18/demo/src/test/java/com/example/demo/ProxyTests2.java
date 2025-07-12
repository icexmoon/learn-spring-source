package com.example.demo;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.springframework.aop.framework.Advised;
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
public class ProxyTests2 {
    @Component
    @Slf4j
    @Getter
    public static class MyBean {
        protected MyBean2 myBean2;
        protected boolean initialized = false;

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
    public void testProxy() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
        MyBean beanProxy = ctx.getBean(MyBean.class);
        showProxyAndTarget(beanProxy);
        ctx.close();
    }

    public static void showProxyAndTarget(MyBean proxy) throws Exception {
        System.out.println(">>>>> 代理中的成员变量");
        System.out.println("\tinitialized=" + proxy.initialized);
        System.out.println("\tmyBean2=" + proxy.myBean2);

        if (proxy instanceof Advised advised) {
            System.out.println(">>>>> 目标中的成员变量");
            MyBean target = (MyBean) advised.getTargetSource().getTarget();
            System.out.println("\tinitialized=" + target.initialized);
            System.out.println("\tmyBean2=" + target.myBean2);
        }
    }
}
