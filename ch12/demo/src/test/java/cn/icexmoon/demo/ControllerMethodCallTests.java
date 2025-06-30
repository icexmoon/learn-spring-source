package cn.icexmoon.demo;

import lombok.Data;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.ModelFactory;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.*;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @ClassName ControllerMethodCallTests
 * @Description Controller 方法调用流程
 * @Author icexmoon@qq.com
 * @Date 2025/6/30 下午5:35
 * @Version 1.0
 */
public class ControllerMethodCallTests {
    @Data
    @ToString
    private static class User {
        private String name;
        private int age;
    }

    @Log4j2
    @Controller("/hello")
    private static class HelloController {
        @ResponseStatus(HttpStatus.OK)
        @GetMapping
        public String hello(@ModelAttribute("user1") User user) {
            log.info("hello() is called, user:{}", user.toString());
            return "hello";
        }

        @ModelAttribute("user2")
        public User getUser() {
            User user = new User();
            user.setName("Bruce");
            user.setAge(20);
            return user;
        }
    }

    @ControllerAdvice
    private static class MyControllerAdvice{
        @ModelAttribute("user3")
        public User user(){
            User user = new User();
            user.setName("Jack");
            user.setAge(18);
            return user;
        }
    }

    @Configuration
    @Import({HelloController.class, MyControllerAdvice.class})
    private static class Config{}

    @Test
    public void test() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        ServletInvocableHandlerMethod handlerMethod = new ServletInvocableHandlerMethod(new HandlerMethod(
                context.getBean(HelloController.class),
                HelloController.class.getMethod("hello", User.class)
        ));
        // 添加参数解析器组
        handlerMethod.setHandlerMethodArgumentResolvers(getArgumentResolvers(context));
        // 添加数据绑定工厂
        handlerMethod.setDataBinderFactory(new ServletRequestDataBinderFactory(null,null ));
        // 添加参数名称获取
        handlerMethod.setParameterNameDiscoverer(new DefaultParameterNameDiscoverer());
        // 模拟请求
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addParameter("name", "Tom");
        mockHttpServletRequest.addParameter("age", "20");
        ServletWebRequest servletWebRequest = new ServletWebRequest(mockHttpServletRequest);
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        // 添加返回值处理器，不加会报错
        handlerMethod.setHandlerMethodReturnValueHandlers(getReturnValueHandler());
        // 执行控制器方法调用
        handlerMethod.invokeAndHandle(servletWebRequest, mavContainer);
        ModelMap model = mavContainer.getModel();
        System.out.println(model);
        context.close();
    }

    @Test
    public void test2() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(Config.class);
        ServletInvocableHandlerMethod handlerMethod = new ServletInvocableHandlerMethod(new HandlerMethod(
                context.getBean(HelloController.class),
                HelloController.class.getMethod("hello", User.class)
        ));
        // 添加参数解析器组
        handlerMethod.setHandlerMethodArgumentResolvers(getArgumentResolvers(context));
        // 添加数据绑定工厂
        ServletRequestDataBinderFactory dataBinderFactory = new ServletRequestDataBinderFactory(null, null);
        handlerMethod.setDataBinderFactory(dataBinderFactory);
        // 添加参数名称获取
        handlerMethod.setParameterNameDiscoverer(new DefaultParameterNameDiscoverer());
        // 模拟请求
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        mockHttpServletRequest.addParameter("name", "Tom");
        mockHttpServletRequest.addParameter("age", "20");
        ServletWebRequest servletWebRequest = new ServletWebRequest(mockHttpServletRequest);
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        // 添加返回值处理器，不加会报错
        handlerMethod.setHandlerMethodReturnValueHandlers(getReturnValueHandler());
        // 获取模型工厂方法
        RequestMappingHandlerAdapter adapter = new RequestMappingHandlerAdapter();
        adapter.setApplicationContext(context);
        adapter.afterPropertiesSet();
        Method getModelFactory = RequestMappingHandlerAdapter.class.getDeclaredMethod("getModelFactory", HandlerMethod.class, WebDataBinderFactory.class);
        getModelFactory.setAccessible(true);
        ModelFactory modelFactory = (ModelFactory)getModelFactory.invoke(adapter, handlerMethod, dataBinderFactory);
        // 初始化模型
        modelFactory.initModel(servletWebRequest, mavContainer, handlerMethod);
        // 执行控制器方法调用
        handlerMethod.invokeAndHandle(servletWebRequest, mavContainer);
        ModelMap model = mavContainer.getModel();
        System.out.println(model);
        context.close();
    }

    private static HandlerMethodArgumentResolverComposite getArgumentResolvers(AnnotationConfigApplicationContext context) {
        HandlerMethodArgumentResolverComposite composite = new HandlerMethodArgumentResolverComposite();
        composite.addResolvers(
                new RequestParamMethodArgumentResolver(context.getDefaultListableBeanFactory(), false),
                new PathVariableMethodArgumentResolver(),
                new RequestHeaderMethodArgumentResolver(context.getDefaultListableBeanFactory()),
                new ServletCookieValueMethodArgumentResolver(context.getDefaultListableBeanFactory()),
                new ExpressionValueMethodArgumentResolver(context.getDefaultListableBeanFactory()),
                new ServletRequestMethodArgumentResolver(),
                new ServletModelAttributeMethodProcessor(false),
                new RequestResponseBodyMethodProcessor(List.of(new MappingJackson2HttpMessageConverter())),
                new ServletModelAttributeMethodProcessor(true),
                new RequestParamMethodArgumentResolver(context.getDefaultListableBeanFactory(), true)
        );
        return composite;
    }

    private static HandlerMethodReturnValueHandlerComposite getReturnValueHandler() {
        HandlerMethodReturnValueHandlerComposite composite = new HandlerMethodReturnValueHandlerComposite();
        composite.addHandler(new ModelAndViewMethodReturnValueHandler());
        composite.addHandler(new ViewNameMethodReturnValueHandler());
        composite.addHandler(new ServletModelAttributeMethodProcessor(false));
        composite.addHandler(new HttpEntityMethodProcessor(List.of(new MappingJackson2HttpMessageConverter())));
        composite.addHandler(new HttpHeadersReturnValueHandler());
        composite.addHandler(new RequestResponseBodyMethodProcessor(List.of(new MappingJackson2HttpMessageConverter())));
        composite.addHandler(new ServletModelAttributeMethodProcessor(true));
        return composite;
    }
}
