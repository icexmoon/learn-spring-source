package com.example.demo;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.ServletWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DeferredImportSelector;
import org.springframework.context.annotation.Import;
import org.springframework.core.type.AnnotationMetadata;

/**
 * @ClassName MvcTests
 * @Description MVC 相关自动配置
 * @Author icexmoon@qq.com
 * @Date 2025/7/8 下午7:55
 * @Version 1.0
 */
public class MvcTests {
    @Configuration
    @Import(MyImportSelector.class)
    static class Config {
    }

    static class MyImportSelector implements DeferredImportSelector {
        @Override
        public String[] selectImports(AnnotationMetadata importingClassMetadata) {
            return new String[]{
                    ServletWebServerFactoryAutoConfiguration.class.getName(),
                    DispatcherServletAutoConfiguration.class.getName(),
                    WebMvcAutoConfiguration.class.getName(),
                    ErrorMvcAutoConfiguration.class.getName()
            };
        }
    }

    @Test
    public void test() {
        AnnotationConfigServletWebServerApplicationContext context
                = new AnnotationConfigServletWebServerApplicationContext();
        context.register(Config.class);
        context.refresh();
        for (String beanDefinitionName : context.getBeanDefinitionNames()) {
            String resourceDescription = context.getBeanDefinition(beanDefinitionName).getResourceDescription();
            if (resourceDescription != null)
                System.out.printf("bean name: %s, source: %s%n", beanDefinitionName, resourceDescription);
        }
        context.close();
    }
}
