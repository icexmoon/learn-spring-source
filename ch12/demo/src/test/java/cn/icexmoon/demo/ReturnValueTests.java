package cn.icexmoon.demo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.support.HandlerMethodReturnValueHandlerComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.mvc.method.annotation.*;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;
import org.springframework.web.servlet.view.freemarker.FreeMarkerView;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;
import org.springframework.web.util.UrlPathHelper;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

/**
 * @ClassName ReturnValueTests
 * @Description 控制器返回值处理
 * @Author icexmoon@qq.com
 * @Date 2025/6/30 下午8:55
 * @Version 1.0
 */
@Slf4j
public class ReturnValueTests {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {
        private String name;
        private int age;
    }

    @Controller
    @RequestMapping("/test")
    private static class MyController {
        @GetMapping
        public ModelAndView test() {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("name", "Tom");
            modelAndView.setViewName("view1");
            return modelAndView;
        }

        public String test2() {
            return "view2";
        }

        @ModelAttribute
        @RequestMapping("/user")
        public User test3() {
            User user = new User();
            user.setName("Tom");
            user.setAge(18);
            return user;
        }

        @RequestMapping("/user")
        public User test4() {
            User user = new User();
            user.setName("Tom");
            user.setAge(18);
            return user;
        }

        public HttpEntity<User> test5() {
            User user = new User("Jerry", 20);
            return new HttpEntity<>(user);
        }

        public HttpHeaders test6(){
            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Type", "text/html;charset=utf-8");
            return headers;
        }

        @ResponseBody
        public User test7(){
            return new User("Jerry", 20);
        }
    }

    @Configuration
    @Import({MyController.class})
    public static class MyConfig {

        @Bean
        public FreeMarkerConfigurer freeMarkerConfigurer() {
            FreeMarkerConfigurer configurer = new FreeMarkerConfigurer();
            configurer.setDefaultEncoding("utf-8");
            configurer.setTemplateLoaderPath("classpath:templates");
            return configurer;
        }


        @Bean // FreeMarkerView 在借助 Spring 初始化时，会要求 web 环境才会走 setConfiguration, 这里想办法去掉了 web 环境的约束
        public FreeMarkerViewResolver viewResolver(FreeMarkerConfigurer configurer) {
            FreeMarkerViewResolver resolver = new FreeMarkerViewResolver() {
                @Override
                protected AbstractUrlBasedView instantiateView() {
                    FreeMarkerView view = new FreeMarkerView() {
                        @Override
                        protected boolean isContextRequired() {
                            return false;
                        }
                    };
                    view.setConfiguration(configurer.getConfiguration());
                    return view;
                }
            };
            resolver.setContentType("text/html;charset=utf-8");
            resolver.setPrefix("/");
            resolver.setSuffix(".ftl");
            resolver.setExposeSpringMacroHelpers(false);
            return resolver;
        }
    }

    @Test
    public void test() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        MyController myController = context.getBean(MyController.class);
        Method test = MyController.class.getMethod("test");
        Object result = test.invoke(myController);
        // 获取返回值处理器组
        HandlerMethodReturnValueHandlerComposite valueHandlerComposite = getReturnValueHandler();
        HandlerMethod handlerMethod = new HandlerMethod(myController, test);
        MethodParameter returnType = handlerMethod.getReturnType();
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest(), new MockHttpServletResponse());
        // 判断是否支持的返回值类型
        if (valueHandlerComposite.supportsReturnType(returnType)) {
            // 处理返回值
            valueHandlerComposite.handleReturnValue(result, returnType, mavContainer, request);
            ModelMap model = mavContainer.getModel();
            System.out.println(model);
            String viewName = mavContainer.getViewName();
            System.out.println(viewName);
            // 使用模板引擎进行页面渲染
            renderView(context, mavContainer, request);
        }
    }

    @Test
    public void test2() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        MyController myController = context.getBean(MyController.class);
        Method test = MyController.class.getMethod("test2");
        Object result = test.invoke(myController);
        // 获取返回值处理器组
        HandlerMethodReturnValueHandlerComposite valueHandlerComposite = getReturnValueHandler();
        HandlerMethod handlerMethod = new HandlerMethod(myController, test);
        MethodParameter returnType = handlerMethod.getReturnType();
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest(), new MockHttpServletResponse());
        // 判断是否支持的返回值类型
        if (valueHandlerComposite.supportsReturnType(returnType)) {
            // 处理返回值
            valueHandlerComposite.handleReturnValue(result, returnType, mavContainer, request);
            ModelMap model = mavContainer.getModel();
            System.out.println(model);
            String viewName = mavContainer.getViewName();
            System.out.println(viewName);
            // 使用模板引擎进行页面渲染
            renderView(context, mavContainer, request);
        }
    }

    @Test
    public void test3() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        MyController myController = context.getBean(MyController.class);
        Method test = MyController.class.getMethod("test3");
        testModelAttribute(test, myController, context);
    }

    @Test
    public void test4() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        MyController myController = context.getBean(MyController.class);
        Method test = MyController.class.getMethod("test4");
        testModelAttribute(test, myController, context);
    }

    @Test
    public void test5() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        MyController myController = context.getBean(MyController.class);
        Method test = MyController.class.getMethod("test5");
        testNoView(test, myController, context);
    }

    @Test
    public void test6() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        MyController myController = context.getBean(MyController.class);
        Method test = MyController.class.getMethod("test6");
        testNoView(test, myController, context);
    }

    @Test
    public void test7() throws Exception {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(MyConfig.class);
        MyController myController = context.getBean(MyController.class);
        Method test = MyController.class.getMethod("test7");
        testNoView(test, myController, context);
    }

    private static void testNoView(Method test, MyController myController, AnnotationConfigApplicationContext context) throws Exception {
        Object result = test.invoke(myController);
        // 获取返回值处理器组
        HandlerMethodReturnValueHandlerComposite valueHandlerComposite = getReturnValueHandler();
        HandlerMethod handlerMethod = new HandlerMethod(myController, test);
        MethodParameter returnType = handlerMethod.getReturnType();
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        MockHttpServletResponse response = new MockHttpServletResponse();
        ServletWebRequest request = new ServletWebRequest(new MockHttpServletRequest(), response);
        // 判断是否支持的返回值类型
        if (valueHandlerComposite.supportsReturnType(returnType)) {
            // 处理返回值
            valueHandlerComposite.handleReturnValue(result, returnType, mavContainer, request);
            if (!mavContainer.isRequestHandled()) {
                renderView(context, mavContainer, request);
            }
            // 打印响应头
            Collection<String> headerNames = response.getHeaderNames();
            for (String headerName : headerNames) {
                String header = response.getHeader(headerName);
                System.out.printf("%s: %s%n", headerName, header);
            }
            // 打印响应体
            byte[] contentAsByteArray = response.getContentAsByteArray();
            String content = new String(contentAsByteArray, StandardCharsets.UTF_8);
            System.out.println(content);
        }
    }

    private static void testModelAttribute(Method test, MyController myController, AnnotationConfigApplicationContext context) throws Exception {
        Object result = test.invoke(myController);
        // 获取返回值处理器组
        HandlerMethodReturnValueHandlerComposite valueHandlerComposite = getReturnValueHandler();
        HandlerMethod handlerMethod = new HandlerMethod(myController, test);
        MethodParameter returnType = handlerMethod.getReturnType();
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest();
        ServletWebRequest request = new ServletWebRequest(mockHttpServletRequest, new MockHttpServletResponse());
        // 在域中写入路径信息，用于视图匹配
        mockHttpServletRequest.setRequestURI("/test3");
        UrlPathHelper.defaultInstance.resolveAndCacheLookupPath(mockHttpServletRequest);
        // 判断是否支持的返回值类型
        if (valueHandlerComposite.supportsReturnType(returnType)) {
            // 处理返回值
            valueHandlerComposite.handleReturnValue(result, returnType, mavContainer, request);
            ModelMap model = mavContainer.getModel();
            System.out.println(model);
            String viewName = mavContainer.getViewName();
            System.out.println(viewName);
            // 使用模板引擎进行页面渲染
            renderView(context, mavContainer, request);
        }
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


    @SuppressWarnings("all")
    private static void renderView(AnnotationConfigApplicationContext context, ModelAndViewContainer container,
                                   ServletWebRequest webRequest) throws Exception {
        log.debug(">>>>>> 渲染视图");
        FreeMarkerViewResolver resolver = context.getBean(FreeMarkerViewResolver.class);
        String viewName = container.getViewName() != null ? container.getViewName() : new DefaultRequestToViewNameTranslator().getViewName(webRequest.getRequest());
        log.debug("没有获取到视图名, 采用默认视图名: {}", viewName);
        // 每次渲染时, 会产生新的视图对象, 它并非被 Spring 所管理, 但确实借助了 Spring 容器来执行初始化
        View view = resolver.resolveViewName(viewName, Locale.getDefault());
        view.render(container.getModel(), webRequest.getRequest(), webRequest.getResponse());
        System.out.println(new String(((MockHttpServletResponse) webRequest.getResponse()).getContentAsByteArray(), StandardCharsets.UTF_8));
    }

}
