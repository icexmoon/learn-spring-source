package cn.icexmoon.demo2;

import cn.icexmoon.demo2.service.DemoService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class Demo2Application {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(Demo2Application.class, args);
        DemoService demoService = context.getBean(DemoService.class);
        System.out.println(demoService.getClass());
    }

}
