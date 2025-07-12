package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Map;

/**
 * @ClassName ConditionTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/7/11 下午3:18
 * @Version 1.0
 */
public class ConditionTests3 {
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

    @Target({ElementType.TYPE, ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Conditional(ClassExistsCondition.class)
    @interface ConditionalOnClass{
        // 是否希望指定类存在
        boolean exists();
        // 指定类的完整名称
        String className();
    }

    @Configuration
    @ConditionalOnClass(exists = true, className = "com.alibaba.druid.pool.DruidDataSource")
    static class MyAutoConfig1{
        @Bean
        public MyBean1 myBean1(){
            return new MyBean1();
        }
    }

    @Configuration
    @ConditionalOnClass(exists = false, className = "com.alibaba.druid.pool.DruidDataSource")
    static class MyAutoConfig2{
        @Bean
        public MyBean2 myBean2(){
            return new MyBean2();
        }
    }

    static class ClassExistsCondition implements Condition{

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes("com.example.demo.ConditionTests3$ConditionalOnClass");
            boolean exists = (boolean)annotationAttributes.get("exists");
            String className = (String)annotationAttributes.get("className");
            boolean present = ClassUtils.isPresent(className, null);
            return exists == present;
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
