package com.example.demo.event.listener;

import com.example.demo.event.UserUpdatedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * @ClassName UserListener
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/18 下午12:04
 * @Version 1.0
 */
@Component
public class UserListener {
    @EventListener
    public void userUpdatedEventHandler(UserUpdatedEvent userUpdatedEvent){
        Long userId = userUpdatedEvent.getUserId();
        System.out.println("User updated: " + userId);
    }
}
