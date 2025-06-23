package cn.icexmoon.demo3;

import cn.icexmoon.demo3.controller.DemoController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.EnableLoadTimeWeaving;

@SpringBootApplication
@EnableLoadTimeWeaving
public class Demo3Application {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(Demo3Application.class, args);
        DemoController bean = context.getBean(DemoController.class);
        System.out.println(bean.getClass());

    }

}
