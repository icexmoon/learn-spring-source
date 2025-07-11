package com.example.demo;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.ErrorProperties;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletRegistrationBean;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistrarBeanPostProcessor;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.BeanNameViewResolver;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * @ClassName ServletExceptionTests
 * @Description Servlet 异常处理
 * @Author icexmoon@qq.com
 * @Date 2025/7/3 下午12:01
 * @Version 1.0
 */
public class ServletExceptionTests3 {
    /**
     * Servlet 过滤器
     */
    @Slf4j
    @WebFilter("/*")
    public static class MyFilter implements Filter {

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            if (!(request instanceof HttpServletRequest) ||
                    !(response instanceof HttpServletResponse)) {
                throw new RuntimeException("这不是一个 HTTP 请求");
            }
            HttpServletRequest httpServletRequest = (HttpServletRequest) request;
            String url = httpServletRequest.getRequestURI().toString();
            log.debug("MyFilter is called.");
            // 模拟过滤器存在异常的情况
            if (url.equals("/my/hello")) {
                int i = 1 / 0;
            }
            chain.doFilter(request, response);
        }
    }

    @Controller
    @RequestMapping("/my")
    public static class MyController {
        @GetMapping("/hello")
        @ResponseBody
        public Result<String> hello() {
            return Result.success("hello");
        }

        @GetMapping("/bye")
        @ResponseBody
        public Result<String> bye() {
            // 人为制造一个异常
            int i = 1 / 0;
            return Result.success("bye");
        }
    }

//    @Controller
//    @RequestMapping("/error")
//    public static class MyErrorController {
//        @GetMapping
//        @ResponseBody
//        public Result<Void> error(HttpServletRequest request) {
//            Throwable error = (Throwable)request.getAttribute((RequestDispatcher.ERROR_EXCEPTION));
//            return Result.fail(error.getMessage());
//        }
//    }

    @ControllerAdvice
    public static class MyControllerAdvice {
        /**
         * 全局异常处理
         *
         * @param e 异常
         * @return 错误信息
         */
        @ExceptionHandler
        @ResponseBody
        public Result<Void> globalExceptionHandler(Exception e, HttpServletResponse response) {
            return Result.fail(e.getMessage());
        }
    }

    @Configuration
    @ServletComponentScan(basePackageClasses = {MyFilter.class})
    @Import({MyController.class, MyControllerAdvice.class})
    @Slf4j
    public static class WebConfig {
        @Bean
        public ServletWebServerFactory servletContainer() {
            TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory();
            tomcat.setPort(8080);
            return tomcat;
        }

        @Bean
        public DispatcherServlet dispatcherServlet() {
            DispatcherServlet dispatcherServlet = new DispatcherServlet();
            dispatcherServlet.setDetectAllHandlerExceptionResolvers(true);
            return dispatcherServlet;
        }

        @Bean
        public DispatcherServletRegistrationBean servletRegistrationBean() {
            DispatcherServletRegistrationBean dispatcherServletRegistrationBean = new DispatcherServletRegistrationBean(dispatcherServlet(), "/");
            dispatcherServletRegistrationBean.setLoadOnStartup(1);
            return dispatcherServletRegistrationBean;
        }

        @Bean // @RequestMapping
        public RequestMappingHandlerMapping requestMappingHandlerMapping() {
            return new RequestMappingHandlerMapping();
        }

        @Bean // 注意默认的 RequestMappingHandlerAdapter 不会带 jackson 转换器
        public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {
            RequestMappingHandlerAdapter handlerAdapter = new RequestMappingHandlerAdapter();
            handlerAdapter.setMessageConverters(List.of(new MappingJackson2HttpMessageConverter()));
            return handlerAdapter;
        }

        /**
         * 添加异常处理器，默认的 ExceptionHandlerExceptionResolver 没有 JSON 消息转换器
         *
         * @return 异常处理器
         */
        @Bean
        public ExceptionHandlerExceptionResolver exceptionResolver() {
            ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver();
            exceptionResolver.setMessageConverters(List.of(new MappingJackson2HttpMessageConverter()));
            return exceptionResolver;
        }

        /**
         * 错误页面注册器，用于为 Tomcat 注册处理错误的页面地址
         *
         * @return
         */
        @Bean
        public ErrorPageRegistrar errorPageRegistrar() {
            return new ErrorPageRegistrar() {
                @Override
                public void registerErrorPages(ErrorPageRegistry registry) {
                    registry.addErrorPages(new ErrorPage("/error"));
                }
            };
        }

        /**
         * 为 Tomcat 服务器应用错误页面注册器的 bean 后处理器
         *
         * @return
         */
        @Bean
        public ErrorPageRegistrarBeanPostProcessor errorPageRegistrarBeanPostProcessor() {
            return new ErrorPageRegistrarBeanPostProcessor();
        }

        @Bean
        public BasicErrorController basicErrorController() {
            ErrorProperties errorProperties = new ErrorProperties();
            errorProperties.setIncludeException(true);
            return new BasicErrorController(new DefaultErrorAttributes(), errorProperties);
        }

        @Bean
        public View error() {
            return new View() {
                @Override
                public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
                    System.out.println(model);
                    response.setContentType("text/html;charset=UTF-8");
                    response.getWriter().print("""
                            <h1>服务器内部错误</h1>
                            <ul>
                            <li>时间：%s</li>
                            <li>状态码：%s</li>
                            <li>错误信息：%s</li>
                            <li>异常：%s</li>
                            <li>请求路径：%s</li>
                            </ul>
                            """.formatted(model.get("timestamp"),
                            model.get("status"),
                            model.get("error"),
                            model.get("exception"),
                            model.get("path")));
                }
            };
        }

        @Bean
        public ViewResolver viewResolver() {
            return new BeanNameViewResolver();
        }
    }

    @Test
    public void test() throws InterruptedException {
        AnnotationConfigServletWebServerApplicationContext context = new AnnotationConfigServletWebServerApplicationContext(WebConfig.class);
        RequestMappingHandlerMapping handlerMapping = context.getBean(RequestMappingHandlerMapping.class);
        handlerMapping.getHandlerMethods().forEach((RequestMappingInfo k, HandlerMethod v) -> {
            System.out.println("映射路径:" + k + "\t方法信息:" + v);
        });
        new CountDownLatch(1).await();
    }
}
