package com.example.demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping;
import org.springframework.web.servlet.mvc.Controller;
import org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter;

import java.util.concurrent.CountDownLatch;

/**
 * @ClassName BeanNameMappingTests
 * @Description 按照 bean name 进行路径映射处理请求
 * @Author icexmoon@qq.com
 * @Date 2025/7/4 上午9:49
 * @Version 1.0
 */
public class BeanNameMappingTests {
    @Component("/hello")
    static class HelloController implements Controller{
        @Override
        public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
            response.getWriter().print("Hello World");
            return null;
        }
    }

    @Configuration
    @Import(HelloController.class)
    static class WebConfig{
        @Bean
        public ServletWebServerFactory serverFactory(){
            return new TomcatServletWebServerFactory();
        }

        @Bean
        public DispatcherServlet dispatcherServlet(){
            return new DispatcherServlet();
        }

        @Bean
        public DispatcherServletRegistrationBean servletRegistrationBean(){
            return new DispatcherServletRegistrationBean(dispatcherServlet(), "/");
        }

        /**
         * 处理器映射器，根据 bean 名称进行路径映射
         * @return
         */
        @Bean
        public BeanNameUrlHandlerMapping beanNameUrlHandlerMapping(){
            return new BeanNameUrlHandlerMapping();
        }

        /**
         * 处理器适配器，可以调用实现了 Controller 接口的控制器
         * @return
         */
        @Bean
        public SimpleControllerHandlerAdapter simpleControllerHandlerAdapter(){
            return new SimpleControllerHandlerAdapter();
        }

        @Bean("/bye")
        public Controller byeController(){
            return new Controller() {
                @Override
                public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    response.getWriter().print("bye");
                    return null;
                }
            };
        }
    }

    @Test
    public void testBeanNameMapping() throws InterruptedException {
        AnnotationConfigServletWebServerApplicationContext context =
                new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);
        new CountDownLatch(1).await();
    }
}
