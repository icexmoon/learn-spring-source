package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.ApplicationEventMulticaster;
import org.springframework.context.event.GenericApplicationListener;
import org.springframework.core.ResolvableType;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * @ClassName EventTests
 * @Description 实现事件发布
 * @Author icexmoon@qq.com
 * @Date 2025/7/16 下午1:02
 * @Version 1.0
 */
@SpringJUnitConfig
@Slf4j
public class EventTests6 {
    @Configuration
    @Import({SendEmailEventListener.class, SendSmsEventListener.class})
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
        public ApplicationEventMulticaster applicationEventMulticaster(ApplicationContext applicationContext, ThreadPoolTaskExecutor taskExecutor) {
            return new AbsMyApplicationEventMulticaster() {
                private final List<GenericApplicationListener> listeners = new ArrayList<>();

                @Override
                public void addApplicationListenerBean(String listenerBeanName) {
                    ApplicationListener<ApplicationEvent> listener = applicationContext.getBean(listenerBeanName, ApplicationListener.class);
                    GenericApplicationListener genericApplicationListener = new GenericApplicationListener() {

                        @Override
                        public void onApplicationEvent(ApplicationEvent event) {
                            listener.onApplicationEvent(event);
                        }

                        @Override
                        public boolean supportsEventType(ResolvableType eventType) {
                            Class<? extends ApplicationListener> clazz = listener.getClass();
                            ResolvableType[] interfaces = ResolvableType.forClass(clazz).getInterfaces();
                            if (interfaces.length == 0) {
                                return false;
                            }
                            ResolvableType generic = interfaces[0].getGeneric();
                            return generic.isAssignableFrom(eventType);
                        }
                    };
                    listeners.add(genericApplicationListener);
                }

                @Override
                public void multicastEvent(ApplicationEvent event, ResolvableType eventType) {
                    for (GenericApplicationListener listener : listeners) {
                        if (listener.supportsEventType(eventType)) {
                            taskExecutor.submit(() -> listener.onApplicationEvent(event));
                        }
                    }
                }
            };
        }
    }

    @Autowired
    private ApplicationEventPublisher eventPublisher;
    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void test() {
        for (String beanDefinitionName : applicationContext.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }
        log.info("do some business.");
        eventPublisher.publishEvent(new AfterDoSomethingEvent(this));
    }

    @Component
    @Slf4j
    static class SendEmailEventListener implements ApplicationListener<AfterDoSomethingEvent> {

        @Override
        public void onApplicationEvent(AfterDoSomethingEvent event) {
            log.info("send email.");
        }
    }

    @Component
    @Slf4j
    static class SendSmsEventListener implements ApplicationListener<AfterDoSomethingEvent> {

        @Override
        public void onApplicationEvent(AfterDoSomethingEvent event) {
            log.info("send sms.");
        }
    }

    static class AfterDoSomethingEvent extends ApplicationEvent {
        public AfterDoSomethingEvent(Object source) {
            super(source);
        }
    }

    abstract static class AbsMyApplicationEventMulticaster implements ApplicationEventMulticaster {

        @Override
        public void addApplicationListener(ApplicationListener<?> listener) {

        }

        @Override
        abstract public void addApplicationListenerBean(String listenerBeanName);

        @Override
        public void removeApplicationListener(ApplicationListener<?> listener) {

        }

        @Override
        public void removeApplicationListenerBean(String listenerBeanName) {

        }

        @Override
        public void removeApplicationListeners(Predicate<ApplicationListener<?>> predicate) {

        }

        @Override
        public void removeApplicationListenerBeans(Predicate<String> predicate) {

        }

        @Override
        public void removeAllListeners() {

        }

        @Override
        public void multicastEvent(ApplicationEvent event) {

        }

        @Override
        abstract public void multicastEvent(ApplicationEvent event, ResolvableType eventType);
    }
}
