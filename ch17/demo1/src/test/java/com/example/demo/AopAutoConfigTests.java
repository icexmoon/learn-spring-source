package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Map;

/**
 * @ClassName ImportTests5
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/7/8 下午3:43
 * @Version 1.0
 */
public class AopAutoConfigTests {
    @Configuration
    @Import(MyImportSelector.class)
    static class Config{}

    static class MyImportSelector implements DeferredImportSelector {

        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{AopAutoConfiguration.class.getName()};
        }
    }

    @Test
    public void test() {
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean(Config.class);
        AnnotationConfigUtils.registerAnnotationConfigProcessors(ctx);
        ctx.refresh();
        for (String beanDefinitionName : ctx.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }
        ctx.close();
    }

    @Test
    public void test2() {
        GenericApplicationContext ctx = new GenericApplicationContext();
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addLast(new MapPropertySource("mapPropertySource",
                Map.of("spring.aop.auto","false")));
        ctx.setEnvironment(environment);
        ctx.registerBean(Config.class);
        AnnotationConfigUtils.registerAnnotationConfigProcessors(ctx);
        ctx.refresh();
        for (String beanDefinitionName : ctx.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }
        ctx.close();
    }

    /**
     * 设置了属性 spring.aop.proxy-target-class=false
     */
    @Test
    public void test3() {
        GenericApplicationContext ctx = new GenericApplicationContext();
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addLast(new MapPropertySource("mapPropertySource",
                Map.of("spring.aop.proxy-target-class","false")));
        ctx.setEnvironment(environment);
        ctx.registerBean(Config.class);
        AnnotationConfigUtils.registerAnnotationConfigProcessors(ctx);
        ctx.refresh();
        for (String beanDefinitionName : ctx.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }
        AnnotationAwareAspectJAutoProxyCreator proxyCreator = (AnnotationAwareAspectJAutoProxyCreator)ctx.getBean("org.springframework.aop.config.internalAutoProxyCreator");
        System.out.println(proxyCreator.isProxyTargetClass());
        ctx.close();
    }

    /**
     * 没有设置属性 spring.aop.proxy-target-class 或属性为 true
     */
    @Test
    public void test4() {
        GenericApplicationContext ctx = new GenericApplicationContext();
        StandardEnvironment environment = new StandardEnvironment();
        ctx.setEnvironment(environment);
        ctx.registerBean(Config.class);
        AnnotationConfigUtils.registerAnnotationConfigProcessors(ctx);
        ctx.refresh();
        for (String beanDefinitionName : ctx.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }
        AnnotationAwareAspectJAutoProxyCreator proxyCreator = (AnnotationAwareAspectJAutoProxyCreator)ctx.getBean("org.springframework.aop.config.internalAutoProxyCreator");
        System.out.println(proxyCreator.isProxyTargetClass());
        ctx.close();
    }
}
