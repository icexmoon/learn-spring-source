package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * @ClassName ExceptionHandlerExceptionResolverTests
 * @Description 处理多层嵌套的异常
 * @Author icexmoon@qq.com
 * @Date 2025/7/2 下午7:48
 * @Version 1.0
 */
public class ExceptionHandlerExceptionResolverTests3 {
    @Controller
    private static class TestController {
        public void test() {
        }

        @ExceptionHandler
        public ModelAndView handleException(IOException ex) {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.setViewName("error");
            modelAndView.addObject("ex", ex);
            return modelAndView;
        }
    }

    @Test
    public void testExceptionHandlerExceptionResolver() throws NoSuchMethodException, UnsupportedEncodingException {
        Exception ex = new Exception(new RuntimeException(new IOException())); // 产生的异常
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
        ModelAndView modelAndView = exceptionResolver.resolveException(request, response, handlerMethod, ex);
        System.out.println(modelAndView.getViewName());
        System.out.println(modelAndView.getModel());
    }
}
