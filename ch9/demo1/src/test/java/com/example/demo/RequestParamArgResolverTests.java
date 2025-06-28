package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.multipart.MultipartFile;

import java.lang.annotation.Annotation;
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
//            methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
            Annotation[] parameterAnnotations = methodParameter.getParameterAnnotations();
            String annotationsStr = Arrays.stream(parameterAnnotations).map(pa -> '@' + pa.annotationType().getSimpleName()).collect(Collectors.joining(" "));
            System.out.println("形参：注解（%s），类型（%s），参数名（%s）".formatted(
                    annotationsStr,
                    methodParameter.getParameterType().getSimpleName(),
                    methodParameter.getParameterName()));
        }

        ctx.close();
    }
}
