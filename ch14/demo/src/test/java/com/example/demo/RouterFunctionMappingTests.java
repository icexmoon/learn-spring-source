package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.function.*;
import org.springframework.web.servlet.function.support.HandlerFunctionAdapter;
import org.springframework.web.servlet.function.support.RouterFunctionMapping;

import java.util.concurrent.CountDownLatch;

/**
 * @ClassName HandlerFunctionTests
 * @Description 使用 HandlerFunction 进行映射
 * @Author icexmoon@qq.com
 * @Date 2025/7/4 上午11:39
 * @Version 1.0
 */
public class RouterFunctionMappingTests {
    @Configuration
    static class WebConfig{
        @Bean
        public ServletWebServerFactory servletContainer() {
            return new TomcatServletWebServerFactory();
        }

        @Bean
        public DispatcherServlet dispatcherServlet(){
            return new DispatcherServlet();
        }

        @Bean
        public DispatcherServletRegistrationBean dispatcherServletRegistrationBean(){
            return new DispatcherServletRegistrationBean(dispatcherServlet(), "/");
        }

        @Bean
        public RouterFunctionMapping routerFunctionMapping(){
            return new RouterFunctionMapping();
        }

        @Bean
        public HandlerFunctionAdapter handlerFunctionAdapter(){
            return new HandlerFunctionAdapter();
        }

        @Bean
        public RouterFunction<ServerResponse> helloRouterFunction(){
            return RouterFunctions.route(RequestPredicates.GET("/hello"), new HandlerFunction<ServerResponse>() {
                @Override
                public ServerResponse handle(ServerRequest request) throws Exception {
                    return ServerResponse.ok().body("Hello World");
                }
            });
        }

        @Bean
        public RouterFunction<ServerResponse> byeRouterFunction(){
            return RouterFunctions.route(RequestPredicates.GET("/bye"), new HandlerFunction<ServerResponse>() {
                @Override
                public ServerResponse handle(ServerRequest request) throws Exception {
                    return ServerResponse.ok().body("bye");
                }
            });
        }
    }

    @Test
    public void test() throws InterruptedException {
        AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);
        new CountDownLatch(1).await();
    }
}
