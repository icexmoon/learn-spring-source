package com.example.demo;

import cn.hutool.core.io.FileUtil;
import com.example.demo.event.UserUpdatedEvent;
import lombok.Cleanup;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.DefaultSingletonBeanRegistry;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {

        ConfigurableApplicationContext context = SpringApplication.run(DemoApplication.class, args);
//        AnnotationConfigApplicationContext acaContext = (AnnotationConfigApplicationContext) context;
//        printBeanDefinitions(acaContext);
//        printBeanObjects(acaContext);
//        printEnvironment(acaContext);
//        testApplicationEventPublisher(acaContext);
//        try {
//            printResources(acaContext);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//        printClassPaths();
//        testMessageResource(acaContext);
    }

    /**
     * 打印 Bean 对象
     *
     * @param acaContext 容器
     */
    private static void printBeanObjects(AnnotationConfigApplicationContext acaContext) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) acaContext.getBeanFactory();
        DefaultSingletonBeanRegistry registry = beanFactory;
        String[] singletonNames = registry.getSingletonNames();
        for (String name : singletonNames) {
            System.out.println(registry.getSingleton(name));
        }
    }

    /**
     * 打印 bean 定义
     *
     * @param acaContext 容器
     */
    private static void printBeanDefinitions(AnnotationConfigApplicationContext acaContext) {
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) acaContext.getBeanFactory();
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanFactory.getBean(beanDefinitionName));
        }
    }

    private static void printEnvironment(EnvironmentCapable environmentCapable) {
        Environment environment = environmentCapable.getEnvironment();
        String property = environment.getProperty("spring.application.name");
        System.out.println(property);
        String javaHome = environment.getProperty("java_home");
        System.out.println(javaHome);
    }

    private static void testApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        applicationEventPublisher.publishEvent(new UserUpdatedEvent(applicationEventPublisher, 1L));
    }

    private static void printResource(ResourcePatternResolver resourcePatternResolver) throws IOException {
        Resource resource = resourcePatternResolver.getResource("classpath:application.properties");
        @Cleanup BufferedReader reader = FileUtil.getReader(resource.getFile(), StandardCharsets.UTF_8);
        do {
            String line = reader.readLine();
            System.out.println(line);
        }
        while (reader.ready());
    }

    private static void printResources(ResourcePatternResolver resourcePatternResolver) throws IOException {
        Resource[] resources = resourcePatternResolver.getResources("classpath*:META-INF/spring.factories");
        for (Resource resource : resources) {
            System.out.println(resource.getFilename());
        }
    }

    private static void printClassPaths() {
        String classpath = System.getProperty("java.class.path");
        String[] classpathArr = classpath.split(";");
        for (String classpathStr : classpathArr) {
            System.out.println(classpathStr);
        }
    }

    private static void testMessageResource(MessageSource messageSource) {
        String enTitle = messageSource.getMessage("login.title", null, Locale.US);
        String enUserName = messageSource.getMessage("login.username", null, Locale.US);
        String cnTitle = messageSource.getMessage("login.title", null, Locale.CHINA);
        String cnUserName = messageSource.getMessage("login.username", null, Locale.CHINA);
        System.out.println(String.format("%s %s", enTitle, enUserName));
        System.out.println(String.format("%s %s", cnTitle, cnUserName));
    }
}
