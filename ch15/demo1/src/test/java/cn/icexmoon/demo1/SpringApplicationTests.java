package cn.icexmoon.demo1;

import cn.icexmoon.demo1.entity.Teacher;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

/**
 * @ClassName SpringApplicationTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/7/6 下午3:23
 * @Version 1.0
 */
public class SpringApplicationTests {
    @Data
    @AllArgsConstructor
    static class User{
        private String name;
        private int age;
    }

    @Configuration
    public static class Config{
        @Bean
        public User user(){
            return new User("Tom", 20);
        }

        @Bean
        public ServletWebServerFactory servletWebServerFactory(){
            return new TomcatServletWebServerFactory();
        }
    }

    @Test
    public void test() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SpringApplication application = new SpringApplication(Config.class);
        application.setSources(Set.of("classpath:applicationContext.xml"));
        // 创建容器，打印 bean 定义
        ConfigurableApplicationContext context = application.run();
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            String resourceDescription = context.getBeanFactory().getBeanDefinition(beanDefinitionName).getResourceDescription();
            System.out.printf("{bean 名称：%s，来源：%s%n}", beanDefinitionName, resourceDescription);
        }
    }

    @Test
    public void test2() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        // 判断应用类型
        Method deduceFromClasspath = WebApplicationType.class.getDeclaredMethod("deduceFromClasspath");
        deduceFromClasspath.setAccessible(true);
        WebApplicationType applicationType = (WebApplicationType)deduceFromClasspath.invoke(null);
        System.out.println(applicationType);
    }

    @Test
    public void test3() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SpringApplication application = new SpringApplication(Config.class);
        application.setSources(Set.of("classpath:applicationContext.xml"));
        // 添加容器初始化器
        application.addInitializers(new ApplicationContextInitializer<ConfigurableApplicationContext>() {
            @Override
            public void initialize(ConfigurableApplicationContext applicationContext) {
                if (applicationContext instanceof GenericApplicationContext genericApplicationContext) {
                    genericApplicationContext.registerBean("teacher", Teacher.class);;
                }
            }
        });
        // 创建容器，打印 bean 定义
        ConfigurableApplicationContext context = application.run();
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            String resourceDescription = context.getBeanFactory().getBeanDefinition(beanDefinitionName).getResourceDescription();
            System.out.printf("{bean 名称：%s，来源：%s%n}", beanDefinitionName, resourceDescription);
        }
    }

    @Test
    public void test4() {
        SpringApplication application = new SpringApplication(Config.class);
        application.setSources(Set.of("classpath:applicationContext.xml"));
        // 添加监听器
        application.addListeners(new ApplicationListener<ApplicationEvent>() {
            @Override
            public void onApplicationEvent(ApplicationEvent event) {
                System.out.println("产生事件："+event.getClass());
            }
        });
        // 创建容器，打印 bean 定义
        ConfigurableApplicationContext context = application.run();
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            String resourceDescription = context.getBeanFactory().getBeanDefinition(beanDefinitionName).getResourceDescription();
            System.out.printf("{bean 名称：%s，来源：%s%n}", beanDefinitionName, resourceDescription);
        }
    }

    @Test
    public void test5() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        SpringApplication application = new SpringApplication(Config.class);
        Method deduceMainApplicationClass = SpringApplication.class.getDeclaredMethod("deduceMainApplicationClass");
        deduceMainApplicationClass.setAccessible(true);
        Class mainClass = (Class)deduceMainApplicationClass.invoke(application);
        System.out.println(mainClass);
    }
}
