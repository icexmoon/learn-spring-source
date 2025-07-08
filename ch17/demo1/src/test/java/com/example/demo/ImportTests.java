package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ConfigurationClassPostProcessor;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.GenericApplicationContext;

/**
 * @ClassName ImportTests
 * @Description 导入第三方配置类
 * @Author icexmoon@qq.com
 * @Date 2025/7/8 下午12:20
 * @Version 1.0
 */
public class ImportTests {
    static class OuterBean1 {
    }

    static class OuterBean2 {
    }

    /**
     * 外部配置类1
     */
    @Configuration
    static class OuterConfig1 {
        @Bean
        public OuterBean1 outerBean1() {
            return new OuterBean1();
        }
    }

    /**
     * 外部配置类2
     */
    @Configuration
    static class OuterConfig2 {
        @Bean
        public OuterBean2 outerBean2() {
            return new OuterBean2();
        }
    }

    /**
     * 本项目的配置类
     */
    @Configuration
    @Import({OuterConfig1.class, OuterConfig2.class})
    static class Config {}

    @Test
    public void test() {
        GenericApplicationContext ctx = new GenericApplicationContext();
        ctx.registerBean(ConfigurationClassPostProcessor.class);
        ctx.registerBean(Config.class);
        ctx.refresh();
        for (String beanDefinitionName : ctx.getBeanDefinitionNames()) {
            System.out.println(beanDefinitionName);
        }
        ctx.close();
    }
}
