package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.concurrent.CountDownLatch;

/**
 * @ClassName TomcatTests
 * @Description 利用 Spring 容器启动一个 Tomcat server
 * @Author icexmoon@qq.com
 * @Date 2025/6/27 上午11:39
 * @Version 1.0
 */
public class TomcatTests {
    @Configuration
    static class Config {
        /**
         * Web Server，这里使用 Tomcat
         * @return
         */
        @Bean
        public ServletWebServerFactory servletContainer() {
            return new TomcatServletWebServerFactory();
        }

        /**
         * 作为入口的 Servlet
         * @return
         */
        @Bean
        public DispatcherServlet dispatcherServlet() {
            return new DispatcherServlet();
        }

        /**
         * 将 Servlet 注册到 Web Server
         * @return
         */
        @Bean
        public ServletRegistrationBean<DispatcherServlet> servletRegistrationBean() {
            return new ServletRegistrationBean<>(dispatcherServlet(), "/");
        }
    }

    @Test
    public void test() throws InterruptedException {
        AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(Config.class);
        new CountDownLatch(1).await();
    }
}
