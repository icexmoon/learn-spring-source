package cn.icexmoon.demo;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBindingPostProcessor;
import org.springframework.context.annotation.CommonAnnotationBeanPostProcessor;
import org.springframework.context.annotation.ContextAnnotationAutowireCandidateResolver;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * @ClassName PostProsessorUsageTests
 * @Description 展示 bean 后处理器的用途
 * @Author icexmoon@qq.com
 * @Date 2025/6/20 下午3:17
 * @Version 1.0
 */
public class PostProcessorUsageTests {
    static class Bean1 {
    }

    static class Bean2 {
    }

    @Getter
    @ToString
    static class Bean3 {
        private Bean1 bean1;
        private Bean2 bean2;
        private String javaHome;

        @Autowired
        public void setBean1(Bean1 bean1) {
            System.out.println("setBean1() is called.");
            this.bean1 = bean1;
        }

        @Resource
        public void setBean2(Bean2 bean2) {
            System.out.println("setBean2() is called.");
            this.bean2 = bean2;
        }

        @Autowired
        public void setJavaHome(@Value("${JAVA_HOME}") String javaHome) {
            System.out.println("setJavaHome() is called.");
            this.javaHome = javaHome;
        }

        @PostConstruct
        public void postConstruct() {
            System.out.println("postConstruct() is called.");
        }

        @PreDestroy
        public void preDestroy() {
            System.out.println("preDestroy() is called.");
        }
    }

    @Test
    public void test() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("bean1", Bean1.class);
        context.registerBean("bean2", Bean2.class);
        context.registerBean("bean3", Bean3.class);
        // 调用工厂的后处理器，添加 bean 的后处理器，实例化 bean 等
        context.refresh();
        Bean3 bean3 = context.getBean(Bean3.class);
        System.out.println(bean3);
        // 关闭容器
        context.close();
    }

    @Test
    public void test2() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("bean1", Bean1.class);
        context.registerBean("bean2", Bean2.class);
        context.registerBean("bean3", Bean3.class);
        // 添加处理 @Autowired 注解的后处理器
        context.registerBean(AutowiredAnnotationBeanPostProcessor.class);
        // 添加处理 @Value 注解的从环境变量、配置中解析字符串的解析器
        context.getDefaultListableBeanFactory().setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        // 调用工厂的后处理器，添加 bean 的后处理器，实例化 bean 等
        context.refresh();
        Bean3 bean3 = context.getBean(Bean3.class);
        System.out.println(bean3);
        // 关闭容器
        context.close();
    }

    @Test
    public void test3() {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("bean1", Bean1.class);
        context.registerBean("bean2", Bean2.class);
        context.registerBean("bean3", Bean3.class);
        // 添加处理 @Autowired 注解的后处理器
        context.registerBean(AutowiredAnnotationBeanPostProcessor.class);
        // 添加处理 @Value 注解的从环境变量、配置中解析字符串的解析器
        context.getDefaultListableBeanFactory().setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        // 添加处理 @Resource 注解和生命周期钩子注解的后处理器
        context.registerBean(CommonAnnotationBeanPostProcessor.class);
        // 调用工厂的后处理器，添加 bean 的后处理器，实例化 bean 等
        context.refresh();
        Bean3 bean3 = context.getBean(Bean3.class);
        System.out.println(bean3);
        // 关闭容器
        context.close();
    }

    @ConfigurationProperties(prefix = "myapp")
    @ToString
    @Setter
    static class Bean4{
        private String home;
        private String version;
    }

    @Test
    public void test4() throws IOException {
        GenericApplicationContext context = new GenericApplicationContext();
        context.registerBean("bean1", Bean1.class);
        context.registerBean("bean2", Bean2.class);
        context.registerBean("bean3", Bean3.class);
        context.registerBean("bean4", Bean4.class);
        // 添加处理 @Autowired 注解的后处理器
        context.registerBean(AutowiredAnnotationBeanPostProcessor.class);
        // 添加处理 @Value 注解的从环境变量、配置中解析字符串的解析器
        context.getDefaultListableBeanFactory().setAutowireCandidateResolver(new ContextAnnotationAutowireCandidateResolver());
        // 添加处理 @Resource 注解和生命周期钩子注解的后处理器
        context.registerBean(CommonAnnotationBeanPostProcessor.class);
        // 添加处理 @ConfigurationProperties 注解的后处理器
        ConfigurationPropertiesBindingPostProcessor.register(context.getDefaultListableBeanFactory());
        // 1. 创建属性源
        org.springframework.core.io.Resource resource = new ClassPathResource("application.properties");
        Properties properties = PropertiesLoaderUtils.loadProperties(resource);
        // 2. 将属性添加到 Environment
        MutablePropertySources propertySources = context.getEnvironment().getPropertySources();
        propertySources.addFirst(new PropertiesPropertySource("appConfig", properties));
        // 调用工厂的后处理器，添加 bean 的后处理器，实例化 bean 等
        context.refresh();
        Bean3 bean3 = context.getBean(Bean3.class);
        System.out.println(bean3);
        Bean4 bean4 = context.getBean(Bean4.class);
        System.out.println(bean4);
        // 关闭容器
        context.close();
    }
}
