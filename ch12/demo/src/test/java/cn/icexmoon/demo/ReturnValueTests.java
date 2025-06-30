package cn.icexmoon.demo;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
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
