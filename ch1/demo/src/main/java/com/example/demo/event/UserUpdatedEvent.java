package com.example.demo.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @ClassName UserUpdatedEvent
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/18 上午11:58
 * @Version 1.0
 */
public class UserUpdatedEvent extends ApplicationEvent {
    @Getter
    private final Long userId;
    public UserUpdatedEvent(Object source, Long userId) {
        super(source);
        this.userId = userId;
    }
}
