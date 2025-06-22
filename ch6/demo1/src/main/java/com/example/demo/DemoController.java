package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName DemoController
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/22 下午5:13
 * @Version 1.0
 */
@RestController
public class DemoController {
    @Autowired
    private DemoService demoService;

    @GetMapping("/test")
    public String test() {
        demoService.doSomething();
        return "AOP 测试成功";
    }
}
