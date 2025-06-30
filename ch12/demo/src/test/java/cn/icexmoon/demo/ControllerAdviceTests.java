package cn.icexmoon.demo;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.method.ControllerAdviceBean;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName ControllerAdviceTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/30 下午4:16
 * @Version 1.0
 */
@Log4j2
public class ControllerAdviceTests {
    @Controller
    private static class Controller1 {
        @InitBinder
        public void initBinder(WebDataBinder binder) {
        }

        public String hello(String name) {
            return "hello " + name;
        }
    }

    @Controller
    private static class Controller2 {
        @InitBinder
        public void initBinder1(WebDataBinder binder) {
        }

        @InitBinder
        public void initBinder2(WebDataBinder binder) {
        }

        public String bye(String name) {
            return "bye " + name;
        }
    }

    @ControllerAdvice
    private static class MyControllerAdvice {
        @InitBinder
        public void globalInitBinder(WebDataBinder binder) {
        }
    }

    @Configuration
    @Import({Controller1.class, Controller2.class, MyControllerAdvice.class})
    private static class WebConfig {

    }

    @Test
    public void test() throws NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(WebConfig.class);
        RequestMappingHandlerAdapter requestMappingHandlerAdapter = new RequestMappingHandlerAdapter();
        requestMappingHandlerAdapter.setApplicationContext(context);
        requestMappingHandlerAdapter.afterPropertiesSet();
        showBindMethods(requestMappingHandlerAdapter);
        log.info("模拟控制器1方法调用");
        Method getDataBinderFactory = RequestMappingHandlerAdapter.class.getDeclaredMethod("getDataBinderFactory", HandlerMethod.class);
        getDataBinderFactory.setAccessible(true);
        getDataBinderFactory.invoke(requestMappingHandlerAdapter, new HandlerMethod(context.getBean(Controller1.class), Controller1.class.getMethod("hello", String.class)));
        showBindMethods(requestMappingHandlerAdapter);
        log.info("模拟控制器2方法调用");
        getDataBinderFactory.invoke(requestMappingHandlerAdapter, new HandlerMethod(context.getBean(Controller2.class), Controller2.class.getMethod("bye", String.class)));
        showBindMethods(requestMappingHandlerAdapter);

    }

    private static void showBindMethods(RequestMappingHandlerAdapter handlerAdapter) throws NoSuchFieldException, IllegalAccessException {
        Field initBinderAdviceCache = RequestMappingHandlerAdapter.class.getDeclaredField("initBinderAdviceCache");
        initBinderAdviceCache.setAccessible(true);
        Map<ControllerAdviceBean, Set<Method>> globalMap = (Map<ControllerAdviceBean, Set<Method>>) initBinderAdviceCache.get(handlerAdapter);
        log.info("全局的 @InitBinder 方法 {}",
                globalMap.values().stream()
                        .flatMap(ms -> ms.stream().map(m -> m.getName()))
                        .collect(Collectors.toList())
        );

        Field initBinderCache = RequestMappingHandlerAdapter.class.getDeclaredField("initBinderCache");
        initBinderCache.setAccessible(true);
        Map<Class<?>, Set<Method>> controllerMap = (Map<Class<?>, Set<Method>>) initBinderCache.get(handlerAdapter);
        log.info("控制器的 @InitBinder 方法 {}",
                controllerMap.entrySet().stream()
                        .flatMap(e -> e.getValue().stream().map(v -> e.getKey().getSimpleName() + "." + v.getName()))
                        .collect(Collectors.toList())
        );
    }
}
