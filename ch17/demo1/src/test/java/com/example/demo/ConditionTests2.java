package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

/**
 * @ClassName ConditionTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/7/11 下午3:18
 * @Version 1.0
 */
public class ConditionTests2 {
    @Configuration
    @Import(MyImportSelector.class)
    static class Config{}

    static class MyImportSelector implements DeferredImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {

            return new String[]{MyAutoConfig1.class.getName(), MyAutoConfig2.class.getName()};
        }
    }

    static class MyBean1{}
    static class MyBean2{}

    @Configuration
    @Conditional(MyCondition1.class)
    static class MyAutoConfig1{
        @Bean
        public MyBean1 myBean1(){
            return new MyBean1();
        }
    }

    @Configuration
    @Conditional(MyCondition2.class)
    static class MyAutoConfig2{
        @Bean
        public MyBean2 myBean2(){
            return new MyBean2();
        }
    }

    static class MyCondition1 implements Condition{

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return ClassUtils.isPresent("com.alibaba.druid.pool.DruidDataSource", null);
        }
    }

    static class MyCondition2 implements Condition{

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            return !ClassUtils.isPresent("com.alibaba.druid.pool.DruidDataSource", null);
        }
    }

    @Test
    public void testCondition() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean(Config.class );
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.refresh();
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
        context.close();
    }
}
