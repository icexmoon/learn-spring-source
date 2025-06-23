package cn.icexmoon.demo3.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName DemoController
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/22 下午8:25
 * @Version 1.0
 */
@Controller
@RequestMapping("/demo")
@Slf4j
public class DemoController {
    @GetMapping
    @ResponseBody
    public String demo() {
        log.info("demo() is called.");
        return "demo";
    }
}
