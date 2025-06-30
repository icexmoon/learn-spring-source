package cn.icexmoon.demo1;

import lombok.Setter;
import lombok.ToString;
import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.boot.convert.ApplicationConversionService;
import org.springframework.format.Formatter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.support.DefaultFormattingConversionService;
import org.springframework.format.support.FormattingConversionService;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.ServletRequestParameterPropertyValues;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ServletRequestDataBinderFactory;

import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @ClassName DataBinderTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/30 上午11:49
 * @Version 1.0
 */
public class DataBinderFactoryTests {
    @Setter
    @ToString
    private static class User {
        private String name;
        private int age;
        private Date birthday;
    }

    @Test
    public void test() throws Exception {
        User user = new User();
        List<InvocableHandlerMethod> methods = new ArrayList<>();
        ServletRequestDataBinderFactory dataBinderFactory = new ServletRequestDataBinderFactory(methods, null);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("name", "Tom");
        request.addParameter("age", "20");
        request.addParameter("birthday", "2000/1/1");
        WebDataBinder webDataBinder = dataBinderFactory.createBinder(new ServletWebRequest(request), user, "user");
        webDataBinder.bind(new ServletRequestParameterPropertyValues(request));
        System.out.println(user);
    }

    @Log4j2
    private static class MyDateFormatter implements Formatter<Date> {
        private String desc;

        private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        public MyDateFormatter(String desc) {
            this.desc = desc;
        }

        public MyDateFormatter() {
        }

        @Override
        public Date parse(String text, Locale locale) throws ParseException {
            if (desc != null && !desc.isEmpty()) {
                log.info("parsed by {}", desc);
            }
            return sdf.parse(text);
        }

        @Override
        public String print(Date object, Locale locale) {
            return sdf.format(object);
        }
    }

    @Controller
    private static class TestController {
        @InitBinder
        public void initBinder(WebDataBinder binder) {
            binder.addCustomFormatter(new MyDateFormatter("通过 InitBinder 方法扩展"));
        }
    }

    /**
     * 通过 initBinder 方法扩展 DataBinder
     *
     * @throws Exception
     */
    @Test
    public void test2() throws Exception {
        User user = new User();
        Method initBinderMethod = TestController.class.getMethod("initBinder", WebDataBinder.class);
        List<InvocableHandlerMethod> methods = List.of(new InvocableHandlerMethod(new TestController(), initBinderMethod));
        ServletRequestDataBinderFactory dataBinderFactory = new ServletRequestDataBinderFactory(methods, null);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("name", "Tom");
        request.addParameter("age", "20");
        request.addParameter("birthday", "2000-01-01");
        WebDataBinder webDataBinder = dataBinderFactory.createBinder(new ServletWebRequest(request), user, "user");
        webDataBinder.bind(new ServletRequestParameterPropertyValues(request));
        System.out.println(user);
    }

    /**
     * 通过 ConversionService 完成扩展
     *
     * @throws Exception
     */
    @Test
    public void test3() throws Exception {
        User user = new User();
        ConfigurableWebBindingInitializer webBindingInitializer = new ConfigurableWebBindingInitializer();
        FormattingConversionService conversionService = new FormattingConversionService();
        conversionService.addFormatter(new MyDateFormatter());
        webBindingInitializer.setConversionService(conversionService);
        ServletRequestDataBinderFactory dataBinderFactory = new ServletRequestDataBinderFactory(null, webBindingInitializer);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("name", "Tom");
        request.addParameter("age", "20");
        request.addParameter("birthday", "2000-01-01");
        WebDataBinder webDataBinder = dataBinderFactory.createBinder(new ServletWebRequest(request), user, "user");
        webDataBinder.bind(new ServletRequestParameterPropertyValues(request));
        System.out.println(user);
    }

    /**
     * 同时使用两种方式扩展
     *
     * @throws Exception
     */
    @Test
    public void test4() throws Exception {
        User user = new User();
        // 通过 initBinder方法扩展
        Method initBinderMethod = TestController.class.getMethod("initBinder", WebDataBinder.class);
        List<InvocableHandlerMethod> methods = List.of(new InvocableHandlerMethod(new TestController(), initBinderMethod));
        // 通过 conversion service 扩展
        ConfigurableWebBindingInitializer webBindingInitializer = new ConfigurableWebBindingInitializer();
        FormattingConversionService conversionService = new FormattingConversionService();
        conversionService.addFormatter(new MyDateFormatter("通过 conversion service 扩展"));
        webBindingInitializer.setConversionService(conversionService);
        ServletRequestDataBinderFactory dataBinderFactory = new ServletRequestDataBinderFactory(methods, webBindingInitializer);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("name", "Tom");
        request.addParameter("age", "20");
        request.addParameter("birthday", "2000-01-01");
        WebDataBinder webDataBinder = dataBinderFactory.createBinder(new ServletWebRequest(request), user, "user");
        webDataBinder.bind(new ServletRequestParameterPropertyValues(request));
        System.out.println(user);
    }

    @Setter
    @ToString
    private static class User2 {
        private String name;
        private int age;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        private Date birthday;
    }

    /**
     * 使用默认的类型转换器
     * @throws Exception
     */
    @Test
    public void test5() throws Exception {
        User2 user = new User2();
        ConfigurableWebBindingInitializer webBindingInitializer = new ConfigurableWebBindingInitializer();
        // 使用默认的转换器
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService();
        webBindingInitializer.setConversionService(conversionService);
        ServletRequestDataBinderFactory dataBinderFactory = new ServletRequestDataBinderFactory(null, webBindingInitializer);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("name", "Tom");
        request.addParameter("age", "20");
        request.addParameter("birthday", "2000-01-01");
        WebDataBinder webDataBinder = dataBinderFactory.createBinder(new ServletWebRequest(request), user, "user");
        webDataBinder.bind(new ServletRequestParameterPropertyValues(request));
        System.out.println(user);
    }

    @Test
    public void test6() throws Exception {
        User2 user = new User2();
        ConfigurableWebBindingInitializer webBindingInitializer = new ConfigurableWebBindingInitializer();
        // 使用默认的转换器
        ApplicationConversionService conversionService = new ApplicationConversionService();
        webBindingInitializer.setConversionService(conversionService);
        ServletRequestDataBinderFactory dataBinderFactory = new ServletRequestDataBinderFactory(null, webBindingInitializer);
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addParameter("name", "Tom");
        request.addParameter("age", "20");
        request.addParameter("birthday", "2000-01-01");
        WebDataBinder webDataBinder = dataBinderFactory.createBinder(new ServletWebRequest(request), user, "user");
        webDataBinder.bind(new ServletRequestParameterPropertyValues(request));
        System.out.println(user);
    }
}
