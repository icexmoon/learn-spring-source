package com.example.demo;

import com.example.demo.util.ResultOkReturnValueResolver;
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
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

/**
 * @ClassName ArgumentResolverTests
 * @Description 自定义参数解析器
 * @Author icexmoon@qq.com
 * @Date 2025/6/27 下午6:52
 * @Version 1.0
 */
public class ReturnResolverTests {
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
            MyRequestMappingHandlerAdapter handlerAdapter = new MyRequestMappingHandlerAdapter();
            // 添加自定义返回值解析器
            handlerAdapter.setCustomReturnValueHandlers(List.of(new ResultOkReturnValueResolver()));
            return handlerAdapter;
        }

    }

    @Test
    public void test() throws Exception {
        AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(Config.class);
        MyRequestMappingHandlerAdapter handlerAdapter = context.getBean(MyRequestMappingHandlerAdapter.class);
        List<HandlerMethodReturnValueHandler> returnValueHandlers = handlerAdapter.getReturnValueHandlers();
        for (HandlerMethodReturnValueHandler returnValueHandler : returnValueHandlers) {
            System.out.println(returnValueHandler);
        }
        RequestMappingHandlerMapping handlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/hello2/info");
        HandlerExecutionChain chain = handlerMapping.getHandler(request);
        HandlerMethod handlerMethod = (HandlerMethod) chain.getHandler();
        MockHttpServletResponse response = new MockHttpServletResponse();
        handlerAdapter.invokeHandlerMethod(request, response, handlerMethod);
        String content = response.getContentAsString();
        System.out.println(content);
//        new CountDownLatch(1).await();
    }
}
