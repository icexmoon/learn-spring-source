package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true) // 强制使用 CGLIB 代理
public class Demo1Application {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(Demo1Application.class, args);
        DemoService demoService = context.getBean(DemoService.class);
        System.out.println(demoService.getClass());
    }

}
