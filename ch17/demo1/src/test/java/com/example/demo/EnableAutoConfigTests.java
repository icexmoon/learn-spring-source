
package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.Map;

/**
 * @ClassName DbTests
 * @Description 事务管理器自动配置
 * @Author icexmoon@qq.com
 * @Date 2025/7/8 下午4:53
 * @Version 1.0
 */
public class EnableAutoConfigTests {
    @Configuration
    @EnableAutoConfiguration
    static class Config {
    }

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


    @Test
    public void test() {
        GenericApplicationContext context = new GenericApplicationContext();
        ConfigurableEnvironment environment = context.getEnvironment();
        environment.getPropertySources().addLast(new MapPropertySource("dataSource", Map.of(
                "spring.datasource.url", "jdbc:mysql://localhost:3306/test",
                "spring.datasource.username", "root",
                "spring.datasource.password", "mysql"
        )));
        AnnotationConfigUtils.registerAnnotationConfigProcessors(context);
        context.registerBean(Config.class);
        AutoConfigurationPackages.register(context, this.getClass().getPackageName());
        context.refresh();
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            String resourceDescription = context.getBeanDefinition(beanDefinitionName).getResourceDescription();
            if (resourceDescription != null)
                System.out.printf("名称：%s，来源：%s%n", beanDefinitionName, resourceDescription);
        }
        context.close();
    }
}
