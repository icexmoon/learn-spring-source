package com.example.demo;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.aot.hint.annotation.Reflective;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.lang.annotation.*;
import java.lang.reflect.Method;

/**
 * @ClassName EventTests
 * @Description EventListener注解原理
 * @Author icexmoon@qq.com
 * @Date 2025/7/16 下午1:02
 * @Version 1.0
 */
@SpringJUnitConfig
@Slf4j
public class EventTests5 {
    @Configuration
    @Import({SmsService.class, EmailService.class})
    public static class Config {
        @Bean
        public ThreadPoolTaskExecutor taskExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
            executor.setMaxPoolSize(10);
            executor.setCorePoolSize(5);
            executor.setQueueCapacity(100);
            return executor;
        }

        @Bean
        public ApplicationEventMulticaster applicationEventMulticaster() {
            SimpleApplicationEventMulticaster simpleApplicationEventMulticaster = new SimpleApplicationEventMulticaster();
            simpleApplicationEventMulticaster.setTaskExecutor(taskExecutor());
            return simpleApplicationEventMulticaster;
        }

        @Bean
        public SmartInitializingSingleton smartInitializingSingleton(ConfigurableApplicationContext applicationContext) {
            return new SmartInitializingSingleton() {
                @Override
                public void afterSingletonsInstantiated() {
                    // 所有单例初始化后调用
                    // 获取所有的 bean
                    for (String beanDefinitionName : applicationContext.getBeanDefinitionNames()) {
                        Object bean = applicationContext.getBean(beanDefinitionName);
                        // 检查 bean 是否有包含了一个参数的 @MyListener 方法
                        Method[] methods = bean.getClass().getMethods();
                        for (Method method : methods) {
                            if (method.isAnnotationPresent(MyListener.class)) {
                                Class<?>[] parameterTypes = method.getParameterTypes();
                                // 检查是不是只有一个形参
                                if (parameterTypes.length != 1) {
                                    continue;
                                }
                                Class<?> eventParameterType = parameterTypes[0];
                                // 检查参数类型是不是 ApplicationEvent
                                if (!ApplicationEvent.class.isAssignableFrom(eventParameterType)) {
                                    continue;
                                }
                                // 为每个方法创建一个 ApplicationListener
                                applicationContext.addApplicationListener(new ApplicationListener<ApplicationEvent>() {
                                    @SneakyThrows
                                    @Override
                                    public void onApplicationEvent(ApplicationEvent event) {
                                        // 判断产生的事件是否与事件处理方法的类型匹配
                                        if (eventParameterType.isAssignableFrom(event.getClass())) {
                                            // 反射调用方法
                                            method.invoke(bean, event);
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            };
        }
    }

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Test
    public void test() {
        log.info("do some business.");
        eventPublisher.publishEvent(new AfterDoSomethingEvent(this));
        eventPublisher.publishEvent(new ApplicationEvent(this) {});
    }

    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @Reflective
    @interface MyListener {
    }

    @Slf4j
    @Component
    static class SmsService {
        @MyListener
        public void handleEvent(AfterDoSomethingEvent event) {
            log.info("send sms.");
        }
    }

    @Slf4j
    @Component
    static class EmailService {
        @MyListener
        public void handleEvent(AfterDoSomethingEvent event) {
            log.info("send email.");
        }
    }

    static class AfterDoSomethingEvent extends ApplicationEvent {
        public AfterDoSomethingEvent(Object source) {
            super(source);
        }
    }
}
