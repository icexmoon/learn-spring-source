package com.example.demo;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ExceptionHandlerExceptionResolverTests
 * @Description 处理多层嵌套的异常
 * @Author icexmoon@qq.com
 * @Date 2025/7/2 下午7:48
 * @Version 1.0
 */
public class ExceptionHandlerExceptionResolverTests5 {
    @Controller
    private static class TestController {
        public void test() {
        }
    }

    @ControllerAdvice
    private static class TestControllerAdvice {
        @ExceptionHandler
        @ResponseBody
        public Map<String, String> handleException(Exception ex, HttpServletRequest request) {
            System.out.println(request);
            return Map.of("error", ex.getMessage());
        }
    }

    @Configuration
    @Import({TestControllerAdvice.class, TestController.class})
    public static class MyConfig{
        @Bean
        public ExceptionHandlerExceptionResolver exceptionHandlerExceptionResolver() {
            ExceptionHandlerExceptionResolver resolver = new ExceptionHandlerExceptionResolver();
            resolver.setMessageConverters(List.of(new MappingJackson2HttpMessageConverter()));
            // 无需调用 afterPropertiesSet 方法，该方法是生命周期方法，会被框架自动调用
            return resolver;
        }
    }

    @Test
    public void testExceptionHandlerExceptionResolver() throws NoSuchMethodException, UnsupportedEncodingException {
        Exception ex = new Exception("test"); // 产生的异常
        TestController controller = new TestController(); // 产生异常的控制器
        // 产生异常的控制器方法
        HandlerMethod handlerMethod = new HandlerMethod(controller, TestController.class.getMethod("test"));
        // 将 ExceptionHandlerExceptionResolver 托管给容器
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        ExceptionHandlerExceptionResolver exceptionResolver = context.getBean(ExceptionHandlerExceptionResolver.class);
        // 模拟请求
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        // 处理异常
        exceptionResolver.resolveException(request, response, handlerMethod, ex);
        System.out.println(response.getContentAsString(StandardCharsets.UTF_8));
    }
}
