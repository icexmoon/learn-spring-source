package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPart;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.support.DefaultDataBinderFactory;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @ClassName RequestParamArgResolver
 * @Description 演示负责处理 @RequestParam 注解的解析器的工作过程
 * @Author icexmoon@qq.com
 * @Date 2025/6/28 下午2:04
 * @Version 1.0
 */
public class RequestParamArgResolverTests {
    @Controller
    @RequestMapping("/test")
    private static class TestController {
        @GetMapping
        public String test(@RequestParam String name,
                           @RequestParam Integer age,
                           String sex,
                           @RequestParam(defaultValue = "${JAVA_HOME}") String javaHome,
                           @RequestParam MultipartFile file) {
            return null;
        }
    }

    @Test
    public void testResolve() throws NoSuchMethodException {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.registerBean(TestController.class);
        ctx.refresh();
        TestController controller = ctx.getBean(TestController.class);
        HandlerMethod handlerMethod = new HandlerMethod(
                controller,
                TestController.class.getMethod("test", String.class, Integer.class, String.class, String.class, MultipartFile.class));
        for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {
            // 添加参数名称解析器
            methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
            Annotation[] parameterAnnotations = methodParameter.getParameterAnnotations();
            String annotationsStr = Arrays.stream(parameterAnnotations).map(pa -> '@' + pa.annotationType().getSimpleName()).collect(Collectors.joining(" "));
            System.out.println("形参：注解（%s），类型（%s），参数名（%s）".formatted(
                    annotationsStr,
                    methodParameter.getParameterType().getSimpleName(),
                    methodParameter.getParameterName()));
        }
        ctx.close();
    }

    @Test
    public void testResolve2() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.registerBean(TestController.class);
        ctx.refresh();
        TestController controller = ctx.getBean(TestController.class);
        HandlerMethod handlerMethod = new HandlerMethod(
                controller,
                TestController.class.getMethod("test", String.class, Integer.class, String.class, String.class, MultipartFile.class));
        // 用于处理 @RequestParam 标记的控制器参数的解析器
        RequestParamMethodArgumentResolver argumentResolver = new RequestParamMethodArgumentResolver(null, false);
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        // 模拟 HTTP 请求
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest("GET", "/test");
        mockHttpServletRequest.addParameter("name", "张三");
        mockHttpServletRequest.addParameter("age", "20");
        mockHttpServletRequest.addParameter("sex", "female");
        mockHttpServletRequest.addPart(new MockPart("file", "test.txt", "hello".getBytes(StandardCharsets.UTF_8)));
        MultipartHttpServletRequest multipartHttpServletRequest = new StandardServletMultipartResolver().resolveMultipart(mockHttpServletRequest);
        ServletWebRequest servletWebRequest = new ServletWebRequest(multipartHttpServletRequest);
        for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {
            // 添加参数名称解析器
            methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
            Annotation[] parameterAnnotations = methodParameter.getParameterAnnotations();
            String annotationsStr = Arrays.stream(parameterAnnotations).map(pa -> '@' + pa.annotationType().getSimpleName()).collect(Collectors.joining(" "));
            System.out.println("==================================");
            System.out.println("形参：注解（%s），类型（%s），参数名（%s）".formatted(
                    annotationsStr,
                    methodParameter.getParameterType().getSimpleName(),
                    methodParameter.getParameterName()));
            if (argumentResolver.supportsParameter(methodParameter)) {
                // 使用参数解析器从 http 请求中解析实参
                Object resolvedArgument = argumentResolver.resolveArgument(methodParameter, mavContainer, servletWebRequest, null);
                System.out.println("实参：类型（%s），值（%s）".formatted(resolvedArgument.getClass().getSimpleName(), resolvedArgument));
            }
        }
        ctx.close();
    }

    @Test
    public void testResolve3() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.registerBean(TestController.class);
        ctx.refresh();
        TestController controller = ctx.getBean(TestController.class);
        HandlerMethod handlerMethod = new HandlerMethod(
                controller,
                TestController.class.getMethod("test", String.class, Integer.class, String.class, String.class, MultipartFile.class));
        // 用于处理 @RequestParam 标记的控制器参数的解析器
        RequestParamMethodArgumentResolver argumentResolver = new RequestParamMethodArgumentResolver(ctx.getBeanFactory(), true);
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        // 模拟 HTTP 请求
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest("GET", "/test");
        mockHttpServletRequest.addParameter("name", "张三");
        mockHttpServletRequest.addParameter("age", "20");
        mockHttpServletRequest.addParameter("sex", "female");
        mockHttpServletRequest.addPart(new MockPart("file", "test.txt", "hello".getBytes(StandardCharsets.UTF_8)));
        MultipartHttpServletRequest multipartHttpServletRequest = new StandardServletMultipartResolver().resolveMultipart(mockHttpServletRequest);
        ServletWebRequest servletWebRequest = new ServletWebRequest(multipartHttpServletRequest);
        // 数据绑定工厂，用于实参的类型转换
        DefaultDataBinderFactory defaultDataBinderFactory = new DefaultDataBinderFactory(null);
        for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {
            // 添加参数名称解析器
            methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
            Annotation[] parameterAnnotations = methodParameter.getParameterAnnotations();
            String annotationsStr = Arrays.stream(parameterAnnotations).map(pa -> '@' + pa.annotationType().getSimpleName()).collect(Collectors.joining(" "));
            System.out.println("==================================");
            System.out.println("形参：注解（%s），类型（%s），参数名（%s）".formatted(
                    annotationsStr,
                    methodParameter.getParameterType().getSimpleName(),
                    methodParameter.getParameterName()));
            if (argumentResolver.supportsParameter(methodParameter)) {
                // 使用参数解析器从 http 请求中解析实参
                Object resolvedArgument = argumentResolver.resolveArgument(methodParameter, mavContainer, servletWebRequest, defaultDataBinderFactory);
                System.out.println("实参：类型（%s），值（%s）".formatted(resolvedArgument.getClass().getSimpleName(), resolvedArgument));
            }
        }
        ctx.close();
    }

    @Test
    public void testResolve4() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.registerBean(TestController.class);
        ctx.refresh();
        TestController controller = ctx.getBean(TestController.class);
        HandlerMethod handlerMethod = new HandlerMethod(
                controller,
                TestController.class.getMethod("test", String.class, Integer.class, String.class, String.class, MultipartFile.class));
        // 用于处理 @RequestParam 标记的控制器参数的解析器
        RequestParamMethodArgumentResolver argumentResolver = new RequestParamMethodArgumentResolver(ctx.getBeanFactory(), true);
        // 使用组合解析器
        HandlerMethodArgumentResolverComposite resolverComposite = new HandlerMethodArgumentResolverComposite();
        resolverComposite.addResolver(argumentResolver);
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        // 模拟 HTTP 请求
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest("GET", "/test");
        mockHttpServletRequest.addParameter("name", "张三");
        mockHttpServletRequest.addParameter("age", "20");
        mockHttpServletRequest.addParameter("sex", "female");
        mockHttpServletRequest.addPart(new MockPart("file", "test.txt", "hello".getBytes(StandardCharsets.UTF_8)));
        MultipartHttpServletRequest multipartHttpServletRequest = new StandardServletMultipartResolver().resolveMultipart(mockHttpServletRequest);
        ServletWebRequest servletWebRequest = new ServletWebRequest(multipartHttpServletRequest);
        // 数据绑定工厂，用于实参的类型转换
        DefaultDataBinderFactory defaultDataBinderFactory = new DefaultDataBinderFactory(null);
        for (MethodParameter methodParameter : handlerMethod.getMethodParameters()) {
            // 添加参数名称解析器
            methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
            Annotation[] parameterAnnotations = methodParameter.getParameterAnnotations();
            String annotationsStr = Arrays.stream(parameterAnnotations).map(pa -> '@' + pa.annotationType().getSimpleName()).collect(Collectors.joining(" "));
            System.out.println("==================================");
            System.out.println("形参：注解（%s），类型（%s），参数名（%s）".formatted(
                    annotationsStr,
                    methodParameter.getParameterType().getSimpleName(),
                    methodParameter.getParameterName()));
            if (resolverComposite.supportsParameter(methodParameter)) {
                // 使用参数解析器从 http 请求中解析实参
                Object resolvedArgument = resolverComposite.resolveArgument(methodParameter, mavContainer, servletWebRequest, defaultDataBinderFactory);
                System.out.println("实参：类型（%s），值（%s）".formatted(resolvedArgument.getClass().getSimpleName(), resolvedArgument));
            }
        }
        ctx.close();
    }
}
