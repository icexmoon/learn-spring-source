package com.example.demo;

import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.*;
import org.springframework.web.servlet.mvc.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @ClassName BeanNameMappingTests
 * @Description 按照 bean name 进行路径映射处理请求
 * @Author icexmoon@qq.com
 * @Date 2025/7/4 上午9:49
 * @Version 1.0
 */
public class BeanNameMappingTests2 {
    @Component("/hello")
    static class HelloController implements Controller {
        @Override
        public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
            response.getWriter().print("Hello World");
            return null;
        }
    }

    @Configuration
    @Import({HelloController.class, MyHandlerMapping.class, MyHandlerAdapter.class})
    static class WebConfig {
        @Bean
        public ServletWebServerFactory serverFactory() {
            return new TomcatServletWebServerFactory();
        }

        @Bean
        public DispatcherServlet dispatcherServlet() {
            return new DispatcherServlet();
        }

        @Bean
        public DispatcherServletRegistrationBean servletRegistrationBean() {
            return new DispatcherServletRegistrationBean(dispatcherServlet(), "/");
        }

        @Bean("/bye")
        public Controller byeController() {
            return new Controller() {
                @Override
                public ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
                    response.getWriter().print("bye");
                    return null;
                }
            };
        }
    }

    @Component
    @Slf4j
    static class MyHandlerMapping implements HandlerMapping {
        @Autowired
        private ApplicationContext applicationContext;
        private Map<String, Controller> urlControllerMap = new HashMap<>();

        @PostConstruct
        public void postConstruct() {
            // 取容器中所有的路径名称控制器 bean，生成路径-控制器映射
            // 获取所有实现了 Controller 接口的 bean
            Map<String, Controller> controllerMap = applicationContext.getBeansOfType(Controller.class);
            if (controllerMap.isEmpty()) {
                return;
            }
            // 过滤掉名称不以 / 开头的 bean
            urlControllerMap = controllerMap.entrySet().stream()
                    .filter(e -> e.getKey().startsWith("/"))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            log.debug("收集到的 url-controller 映射：");
            urlControllerMap.forEach((k, v) -> {
                log.debug("{}->{}", k, v);
            });
        }

        @Override
        public HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
            String url = request.getRequestURI();
            Controller controller = urlControllerMap.get(url);
            log.debug("路径{}匹配到{}", url, controller);
            if (controller == null) {
                return null;
            }
            return new HandlerExecutionChain(controller);
        }
    }

    @Component
    static class MyHandlerAdapter implements HandlerAdapter {

        /**
         * 是否支持控制器方法调用
         *
         * @param handler the handler object to check
         * @return
         */
        @Override
        public boolean supports(Object handler) {
            // 只能调用实现了 Controller 接口的控制器 bean
            return handler instanceof Controller;
        }

        /**
         * 调用控制器方法
         *
         * @param request  current HTTP request
         * @param response current HTTP response
         * @param handler  the handler to use. This object must have previously been passed
         *                 to the {@code supports} method of this interface, which must have
         *                 returned {@code true}.
         * @return
         * @throws Exception
         */
        @Override
        public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
            if (handler instanceof Controller controller) {
                return controller.handleRequest(request, response);
            }
            return null;
        }

        @Override
        public long getLastModified(HttpServletRequest request, Object handler) {
            return -1;
        }
    }

    @Test
    public void testBeanNameMapping() throws InterruptedException {
        AnnotationConfigServletWebServerApplicationContext context =
                new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);
        new CountDownLatch(1).await();
    }
}
