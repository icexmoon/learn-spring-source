package com.example.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName HelloController
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/27 下午3:35
 * @Version 1.0
 */
@Controller
@RequestMapping("/hello")
@Slf4j
public class HelloController {
    @GetMapping
    @ResponseBody
    public String hello() {
        log.info("hello() is called.");
        return "hello";
    }

    @PostMapping("/say")
    @ResponseBody
    public String say(@RequestParam String name) {
        log.info("say(%s) is called.".formatted(name));
        return "say " + name;
    }

    @RequestMapping("/bye")
    @ResponseBody
    public String bye(@RequestParam String name) {
        return "bye " + name;
    }
}
