package com.example.demo;

import com.example.demo.entity.User;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockPart;
import org.springframework.stereotype.Controller;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.DefaultDataBinderFactory;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExpressionValueMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolverComposite;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.*;

import java.lang.annotation.Annotation;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName RequestParamArgResolver
 * @Description 演示负责处理 @RequestParam 注解的解析器的工作过程
 * @Author icexmoon@qq.com
 * @Date 2025/6/28 下午2:04
 * @Version 1.0
 */
public class RequestParamArgResolverTests2 {
    @Controller
    @RequestMapping("/test")
    private static class TestController {
        @GetMapping("/{id}")
        public String test(@RequestParam String name,
                           @RequestParam Integer age,
                           String sex,
                           @RequestParam(defaultValue = "${JAVA_HOME}") String javaHome,
                           @RequestParam MultipartFile file,
                           @PathVariable("id") Integer id,
                           @RequestHeader("Content-type") String contentType,
                           @CookieValue("token") String token,
                           @Value("${JAVA_HOME}") String javaHomeVal,
                           HttpServletRequest httpServletRequest,
                           @ModelAttribute User user,
                           User user2,
                           @RequestBody User user3) {
            return null;
        }
    }

    @Test
    public void test() throws Exception {
        AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
        ctx.registerBean(TestController.class);
        ctx.refresh();
        TestController controller = ctx.getBean(TestController.class);
        HandlerMethod handlerMethod = new HandlerMethod(
                controller,
                TestController.class.getMethod("test", String.class, Integer.class, String.class, String.class, MultipartFile.class,
                        Integer.class, String.class, String.class, String.class, HttpServletRequest.class,
                        User.class, User.class, User.class));
        // 使用组合解析器
        HandlerMethodArgumentResolverComposite resolverComposite = new HandlerMethodArgumentResolverComposite();
        // RequestBody 解析器
        resolverComposite.addResolver(new RequestResponseBodyMethodProcessor(List.of(new MappingJackson2HttpMessageConverter())));
        // RequestParam 解析器
        resolverComposite.addResolver(new RequestParamMethodArgumentResolver(false));
        // 添加 ModelAttribute 解析器
        resolverComposite.addResolver(new ServletModelAttributeMethodProcessor(false));
        // 添加特殊类型解析器
        resolverComposite.addResolver(new ServletRequestMethodArgumentResolver());
        // 添加表达式解析器
        resolverComposite.addResolver(new ExpressionValueMethodArgumentResolver(ctx.getBeanFactory()));
        // 添加用于 Cookie 的解析器
        resolverComposite.addResolver(new ServletCookieValueMethodArgumentResolver(ctx.getBeanFactory()));
        // 添加用于解析路径参数的解析器
        resolverComposite.addResolver(new PathVariableMethodArgumentResolver());
        // 添加解析请求头的解析器
        resolverComposite.addResolver(new RequestHeaderMethodArgumentResolver(ctx.getBeanFactory()));
        resolverComposite.addResolver(new ServletModelAttributeMethodProcessor(true));
        // 不需要使用注解的 RequestParam 解析器
        resolverComposite.addResolver(new RequestParamMethodArgumentResolver(ctx.getBeanFactory(), true));
        ModelAndViewContainer mavContainer = new ModelAndViewContainer();
        // 模拟 HTTP 请求
        MockHttpServletRequest mockHttpServletRequest = new MockHttpServletRequest("GET", "/test/123");
        mockHttpServletRequest.addParameter("name", "张三");
        mockHttpServletRequest.addParameter("age", "20");
        mockHttpServletRequest.addParameter("sex", "female");
        mockHttpServletRequest.addPart(new MockPart("file", "test.txt", "hello".getBytes(StandardCharsets.UTF_8)));
        mockHttpServletRequest.setContentType("application/json");
        // 模拟请求体 JSON
        mockHttpServletRequest.setContent("{\"name\":\"汤姆\",\"age\":15}".getBytes(StandardCharsets.UTF_8));
        // 添加 cookie 信息
        mockHttpServletRequest.setCookies(new Cookie("token", "123abc"));
        // 模拟路径参数解析
        this.parsePathVar(mockHttpServletRequest);
        MultipartHttpServletRequest multipartHttpServletRequest = new StandardServletMultipartResolver().resolveMultipart(mockHttpServletRequest);
        ServletWebRequest servletWebRequest = new ServletWebRequest(multipartHttpServletRequest);
        // 数据绑定工厂，用于实参的类型转换
        DefaultDataBinderFactory defaultDataBinderFactory = new ServletRequestDataBinderFactory(null, null);
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
                System.out.println("实参：类型（%s），值（%s）".formatted(resolvedArgument == null ? "nul" : resolvedArgument.getClass().getSimpleName(), resolvedArgument));
            }
        }
        ctx.close();
    }

    private void parsePathVar(MockHttpServletRequest mockHttpServletRequest) {
        Map<String, String> map = new AntPathMatcher().extractUriTemplateVariables("/test/{id}", "/test/123");
        mockHttpServletRequest.setAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE, map);
    }
}
