package com.example.demo;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter;
import org.springframework.web.servlet.resource.CachingResourceResolver;
import org.springframework.web.servlet.resource.EncodedResourceResolver;
import org.springframework.web.servlet.resource.PathResourceResolver;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.zip.GZIPOutputStream;

/**
 * @ClassName SImpleUrlHandlerMappingTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/7/4 下午12:21
 * @Version 1.0
 */
@Slf4j
public class SimpleUrlHandlerMappingTests2 {
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
            resourceHttpRequestHandler.setResourceResolvers(List.of(
                    new CachingResourceResolver(new ConcurrentMapCache("resourceCache")), // 缓存
                    new EncodedResourceResolver(), // 压缩
                    new PathResourceResolver())); // 获取资源文件
            resourceHttpRequestHandler.setLocations(List.of(new ClassPathResource("html/")));
            return resourceHttpRequestHandler;
        }

        @Bean("/img/**")
        public ResourceHttpRequestHandler imagesResourceHttpRequestHandler() {
            ResourceHttpRequestHandler resourceHttpRequestHandler = new ResourceHttpRequestHandler();
            resourceHttpRequestHandler.setLocations(List.of(new ClassPathResource("images/")));
            return resourceHttpRequestHandler;
        }

        @PostConstruct
        public void postConstruct() throws IOException {
            // 为 html 文件生成压缩文件
            // 获取 html 文件
            ClassPathResource classPathResource = new ClassPathResource("html/");
            File dir = classPathResource.getFile();
            File[] files = dir.listFiles(ff -> ff.getName().endsWith(".html"));
            if (files == null) {
                return;
            }
            for (File file : files) {
                // 生成压缩文件
                FileInputStream fileInputStream = new FileInputStream(file);
                GZIPOutputStream gzipOutputStream = new GZIPOutputStream(new FileOutputStream(file.getAbsolutePath() + ".gz"));
                byte[] buffer = new byte[1024];
                do{
                    int len = fileInputStream.read(buffer);
                    gzipOutputStream.write(buffer, 0, len);
                }
                while (fileInputStream.available() > 0);
                fileInputStream.close();
                gzipOutputStream.close();
            }
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
