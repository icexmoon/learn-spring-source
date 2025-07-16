package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * @ClassName EventTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/7/16 下午1:02
 * @Version 1.0
 */
@SpringJUnitConfig
public class EventTests2 {
    @Configuration
    @Import({SendEmailEventListener.class, SendSmsEventListener.class})
    static class Config{
    }
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Test
    public void test() {
        System.out.println("do some business.");
        eventPublisher.publishEvent(new AfterDoSomethingEvent(this));
    }

    @Component
    static class SendEmailEventListener implements ApplicationListener<AfterDoSomethingEvent> {

        @Override
        public void onApplicationEvent(AfterDoSomethingEvent event) {
            System.out.println("send email.");
        }
    }

    @Component
    static class SendSmsEventListener implements ApplicationListener<AfterDoSomethingEvent>{

        @Override
        public void onApplicationEvent(AfterDoSomethingEvent event) {
            System.out.println("send sms.");
        }
    }

    static class AfterDoSomethingEvent extends ApplicationEvent {
        public AfterDoSomethingEvent(Object source) {
            super(source);
        }
    }
}
