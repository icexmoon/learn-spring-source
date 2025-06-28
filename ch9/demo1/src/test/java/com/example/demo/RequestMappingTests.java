package com.example.demo;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName TomcatTests
 * @Description RequestMapping 工作原理
 * @Author icexmoon@qq.com
 * @Date 2025/6/27 上午11:39
 * @Version 1.0
 */
public class RequestMappingTests {
    @Configuration
    @ComponentScan
    @PropertySource("classpath:application.properties")
    @EnableConfigurationProperties({ServerProperties.class, WebMvcProperties.class})
    static class Config {
        /**
         * Web Server，这里使用 Tomcat
         *
         * @return
         */
        @Bean
        public ServletWebServerFactory servletContainer(ServerProperties serverProperties) {
            return new TomcatServletWebServerFactory(serverProperties.getPort());
        }

        /**
         * 作为入口的 Servlet
         *
         * @return
         */
        @Bean
        public DispatcherServlet dispatcherServlet() {
            return new DispatcherServlet();
        }

        /**
         * 将 Servlet 注册到 Web Server
         *
         * @return
         */
        @Bean
        public ServletRegistrationBean<DispatcherServlet> servletRegistrationBean(WebMvcProperties mvcProperties) {
            ServletRegistrationBean<DispatcherServlet> registrationBean = new ServletRegistrationBean<>(dispatcherServlet(), "/");
            // 立即初始化
            registrationBean.setLoadOnStartup(mvcProperties.getServlet().getLoadOnStartup());
            return registrationBean;
        }

        @Bean
        public MyRequestMappingHandlerAdapter handlerAdapter() {
            return new MyRequestMappingHandlerAdapter();
        }

    }

    @Test
    public void test() throws Exception {
        AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(Config.class);
        RequestMappingHandlerMapping handlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        // 获取请求-方法映射
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        handlerMethods.forEach((k, v) -> {
            System.out.println("请求：%s，处理器：%s".formatted(k, v));
        });
        HttpServletRequest httpRequest = new MockHttpServletRequest("GET", "/hello");
        HandlerExecutionChain executionChain = handlerMapping.getHandler(httpRequest);
        System.out.println(executionChain);
        MyRequestMappingHandlerAdapter handlerAdapter = context.getBean(MyRequestMappingHandlerAdapter.class);
        HttpServletResponse response = new MockHttpServletResponse();
        handlerAdapter.invokeHandlerMethod(httpRequest, response, (HandlerMethod) executionChain.getHandler());
        new CountDownLatch(1).await();
    }

    @Test
    public void test2() throws Exception {
        AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(Config.class);
        RequestMappingHandlerMapping handlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        // 获取请求-方法映射
        Map<RequestMappingInfo, HandlerMethod> handlerMethods = handlerMapping.getHandlerMethods();
        handlerMethods.forEach((k, v) -> {
            System.out.println("请求：%s，处理器：%s".formatted(k, v));
        });
        MockHttpServletRequest httpRequest = new MockHttpServletRequest("POST", "/hello/say");
        httpRequest.setParameter("name", "Tom");
        HandlerExecutionChain executionChain = handlerMapping.getHandler(httpRequest);
        System.out.println(executionChain);
        MyRequestMappingHandlerAdapter handlerAdapter = context.getBean(MyRequestMappingHandlerAdapter.class);
        HttpServletResponse response = new MockHttpServletResponse();
        handlerAdapter.invokeHandlerMethod(httpRequest, response, (HandlerMethod) executionChain.getHandler());
        new CountDownLatch(1).await();
    }

    @Test
    public void test3() throws Exception {
        AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(Config.class);
        MyRequestMappingHandlerAdapter handlerAdapter = context.getBean(MyRequestMappingHandlerAdapter.class);
        // 获取参数解析器
        List<HandlerMethodArgumentResolver> argumentResolvers = handlerAdapter.getArgumentResolvers();
        for (HandlerMethodArgumentResolver argumentResolver : argumentResolvers) {
            System.out.println(argumentResolver);
        }
    }

    @Test
    public void test4() throws Exception {
        AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(Config.class);
        MyRequestMappingHandlerAdapter handlerAdapter = context.getBean(MyRequestMappingHandlerAdapter.class);
        // 获取参数解析器
        List<HandlerMethodReturnValueHandler> returnValueHandlers = handlerAdapter.getReturnValueHandlers();
        for (HandlerMethodReturnValueHandler returnValueHandler : returnValueHandlers) {
            System.out.println(returnValueHandler);
        }
    }
}
