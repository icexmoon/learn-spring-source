package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName SImpleUrlHandlerMappingTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/7/4 下午12:21
 * @Version 1.0
 */
@Slf4j
public class SimpleUrlHandlerMappingTests {
    @Configuration
    static class WebConfig {
        @Bean
        public ServletWebServerFactory servletContainer() {
            return new TomcatServletWebServerFactory();
        }

        @Bean
        public DispatcherServlet dispatcherServlet() {
            return new DispatcherServlet();
        }

        @Bean
        public DispatcherServletRegistrationBean dispatcherServletRegistrationBean() {
            return new DispatcherServletRegistrationBean(dispatcherServlet(), "/*");
        }

        @Bean
        public SimpleUrlHandlerMapping simpleUrlHandlerMapping(ApplicationContext context) {
            SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
            Map<String, ResourceHttpRequestHandler> urlMap = context.getBeansOfType(ResourceHttpRequestHandler.class);
            handlerMapping.setUrlMap(urlMap);
            return handlerMapping;
        }

        @Bean
        public HttpRequestHandlerAdapter httpRequestHandlerAdapter() {
            return new HttpRequestHandlerAdapter();
        }

        @Bean("/**")
        public ResourceHttpRequestHandler htmlResourceHttpRequestHandler() {
            ResourceHttpRequestHandler resourceHttpRequestHandler = new ResourceHttpRequestHandler();
            resourceHttpRequestHandler.setLocations(List.of(new ClassPathResource("html/")));
            return resourceHttpRequestHandler;
        }

        @Bean("/img/**")
        public ResourceHttpRequestHandler imagesResourceHttpRequestHandler() {
            ResourceHttpRequestHandler resourceHttpRequestHandler = new ResourceHttpRequestHandler();
            resourceHttpRequestHandler.setLocations(List.of(new ClassPathResource("images/")));
            return resourceHttpRequestHandler;
        }
    }

    @Test
    public void testSimpleUrlHandlerMapping() throws InterruptedException {
        AnnotationConfigServletWebServerApplicationContext context
                = new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);
        SimpleUrlHandlerMapping handlerMapping = context.getBean(SimpleUrlHandlerMapping.class);
        Map<String, ?> urlMap = handlerMapping.getUrlMap();
        urlMap.forEach((k, v) -> {
            log.debug("{}->{}", k, v);
        });
        new CountDownLatch(1).await();
    }
}
