package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
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
@Slf4j
public class EventTests3 {
    @Configuration
    @Import({SmsService.class, EmailService.class})
    static class Config{
    }
    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Test
    public void test() {
        log.info("do some business.");
        eventPublisher.publishEvent(new AfterDoSomethingEvent(this));
    }

    @Slf4j
    @Component
    static class SmsService{
        @EventListener
        public void handleEvent(AfterDoSomethingEvent event) {
            log.info("send sms.");
        }
    }

    @Slf4j
    @Component
    static class EmailService{
        @EventListener
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
