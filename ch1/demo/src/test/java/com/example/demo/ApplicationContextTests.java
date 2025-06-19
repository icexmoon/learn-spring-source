package com.example.demo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.Controller;

import java.util.concurrent.CountDownLatch;

/**
 * @ClassName ApplicationContextTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/19 下午4:32
 * @Version 1.0
 */
public class ApplicationContextTests {
    static class Bean1{}
    @Getter
    @Setter
    static class Bean2{
        private Bean1 bean1;
    }

    @Test
    public void testXmlClassPathApplicationContext() {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
        Bean2 bean2 = context.getBean(Bean2.class);
        System.out.println(bean2.getBean1());
    }

    @Test
    public void testSystemFilePathApplicationContext() {
        FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("D:\\workspace\\learn-spring-source\\ch1\\demo\\src\\test\\resources\\applicationContext.xml");
        Bean2 bean2 = context.getBean(Bean2.class);
        System.out.println(bean2.getBean1());
    }

    static class Bean3{}

    @AllArgsConstructor
    static class Bean4{
        @Getter
        private Bean3 bean3;
    }

    @Configuration
    static class Config{
        @Bean
        public Bean3 bean3(){
            return new Bean3();
        }

        @Bean
        public Bean4 bean4(){
            return new Bean4(bean3());
        }
    }

    @Test
    public void testAnnotationConfigApplicationContext() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        Bean4 bean4 = context.getBean(Bean4.class);
        System.out.println(bean4.getBean3());
    }

    @Configuration
    static class WebConfig{
        @Bean
        public ServletWebServerFactory servletWebServerFactory(){
            return new TomcatServletWebServerFactory(8081);
        }

        @Bean
        public DispatcherServlet dispatcherServlet(){
            return new DispatcherServlet();
        }

        @Bean
        public DispatcherServletRegistrationBean dispatcherServletRegistrationBean(){
            return new DispatcherServletRegistrationBean(dispatcherServlet(), "/*");
        }

        @Bean("/hello")
        public Controller controller(){
            return (request, response) -> {
                response.getWriter().print("Hello World");
                return null;
            };
        }
    }

    @Test
    public void test() throws InterruptedException {
        AnnotationConfigServletWebServerApplicationContext context =
                new AnnotationConfigServletWebServerApplicationContext();
        context.register(WebConfig.class);
        context.refresh();
        new CountDownLatch(1).await();
    }
}
