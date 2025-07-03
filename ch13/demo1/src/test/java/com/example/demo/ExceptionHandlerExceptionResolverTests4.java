package com.example.demo;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
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
public class ExceptionHandlerExceptionResolverTests4 {
    @Controller
    private static class TestController {
        public void test() {
        }

        @ExceptionHandler
        @ResponseBody
        public Map<String, String> handleException(Exception ex, HttpServletRequest request) {
            System.out.println(request);
            return Map.of("error", ex.getMessage());
        }
    }

    @Test
    public void testExceptionHandlerExceptionResolver() throws NoSuchMethodException, UnsupportedEncodingException {
        Exception ex = new Exception("test"); // 产生的异常
        TestController controller = new TestController(); // 产生异常的控制器
        // 产生异常的控制器方法
        HandlerMethod handlerMethod = new HandlerMethod(controller, TestController.class.getMethod("test"));
        ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver();
        // 为异常处理器设置消息转换器
        exceptionResolver.setMessageConverters(List.of(new MappingJackson2HttpMessageConverter()));
        // 初始化异常处理器，会添加默认的参数解析器和返回值处理器
        exceptionResolver.afterPropertiesSet();
        // 模拟请求
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        // 处理异常
        exceptionResolver.resolveException(request, response, handlerMethod, ex);
        System.out.println(response.getContentAsString(StandardCharsets.UTF_8));
    }
}
