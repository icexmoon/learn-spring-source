package com.example.demo.controller;

import com.example.demo.entity.UserInfo;
import com.example.demo.util.ResultOk;
import com.example.demo.util.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName HelloController
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/27 下午3:35
 * @Version 1.0
 */
@Controller
@RequestMapping("/hello2")
@Slf4j
public class HelloController2 {
    @GetMapping
    @ResponseBody
    public String hello(@Token String token) {
        log.info("hello() is called.");
        log.info("token:%s".formatted(token));
        return "hello";
    }

    @GetMapping("/info")
    @ResultOk
    public UserInfo info() {
        return new UserInfo("张三", 20);
    }
}
