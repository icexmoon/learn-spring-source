package com.example.demo;

import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.AnnotationUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Optional;

/**
 * @ClassName ValueTests
 * @Description @Value 注解的解析过程
 * @Author icexmoon@qq.com
 * @Date 2025/7/12 下午4:23
 * @Version 1.0
 */
public class ValueTests {
    @Target(ElementType.FIELD)
    @Retention(RetentionPolicy.RUNTIME)
    @interface MyValue{
        String value();
    }

    @Data
    @AllArgsConstructor
    static class Student{
        private String name;
        private int age;
    }

    @Configuration(proxyBeanMethods = false)
    @ToString
    @Getter
    static class MyProperties {
        @MyValue("${JAVA_HOME}")
        private String javaHome;
        @MyValue("18")
        private int age;
        @MyValue("#{@student}")
        private Student student;
        @MyValue("#{'hello, ' + '${JAVA_HOME}'}")
        private String hello;
    }

    @Configuration
    @Import({MyProperties.class, MyConfigPropertiesBeanPostProcessor.class})
    static class Config{
        @Bean
        public Student student(){
            return new Student("Tom", 20);
        }
    }

    @Component
    @Slf4j
    static class MyConfigPropertiesBeanPostProcessor implements BeanPostProcessor {
        @Autowired
        private ApplicationContext applicationContext;

        @SneakyThrows
        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            Optional<Component> annotation = AnnotationUtils.findAnnotation(bean.getClass(), Component.class);
            if (annotation.isPresent()) {
                // 存在自定义 @MyConfigurationProperties 注解
                // 处理使用了 @MyValue 的属性
                Field[] declaredFields = bean.getClass().getDeclaredFields();
                for (Field field : declaredFields) {
                    MyValue valueAnnotation = field.getAnnotation(MyValue.class);
                    if (valueAnnotation != null) {
                        log.info("开始处理字段 {}", field.getName());
                        // 获取 @Value 注解的值
                        String value = valueAnnotation.value();
                        log.info("获取到的原始 Value 值：{}", value);
                        // 尝试作为 ${...} 表达式进行解析
                        value = applicationContext.getEnvironment().resolvePlaceholders(value);
                        log.info("尝试作为 ${...} 表达式解析后的值：{}", value);
                        field.setAccessible(true);
                        // 尝试当做 SpEL 表达式 #｛...｝ 进行解析
                        ConfigurableBeanFactory autowireCapableBeanFactory = (ConfigurableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
                        Object evaluatedValue = autowireCapableBeanFactory.getBeanExpressionResolver().evaluate(value, new BeanExpressionContext(autowireCapableBeanFactory, null));
                        log.info("尝试作为 SpEL 表达式进行解析后的值：{}", evaluatedValue);
                        // 尝试类型转换
                        Object convertedValue = autowireCapableBeanFactory.getTypeConverter().convertIfNecessary(evaluatedValue, field.getType());
                        log.info("value 的值从 {} 转换为 {}", evaluatedValue.getClass(), convertedValue.getClass());
                        field.set(bean, convertedValue);
                    }
                }
            }
            return bean;
        }
    }

    @Test
    public void test() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        MyProperties myProperties = context.getBean(MyProperties.class);
        System.out.println(myProperties);
    }
}
