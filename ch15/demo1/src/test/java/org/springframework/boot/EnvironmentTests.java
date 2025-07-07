package org.springframework.boot;

import lombok.Data;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.config.ConfigDataEnvironmentPostProcessor;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySources;
import org.springframework.boot.env.EnvironmentPostProcessorApplicationListener;
import org.springframework.boot.env.RandomValuePropertySourceEnvironmentPostProcessor;
import org.springframework.boot.logging.DeferredLogs;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.ResourcePropertySource;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * @ClassName EnvironmentTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/7/7 下午2:45
 * @Version 1.0
 */
public class EnvironmentTests {
    @Test
    public void test() throws IOException {
        ApplicationEnvironment env = new ApplicationEnvironment();
        env.getPropertySources().addLast(new ResourcePropertySource(new ClassPathResource("application.properties")));
        String[] args = new String[]{
                "--spring.profiles.active=dev",
        };
        env.getPropertySources().addFirst(new SimpleCommandLinePropertySource(args));
        ConfigurationPropertySources.attach(env);
        printPropertySources(env);
        System.out.println(env.getProperty("JAVA_HOME"));
        System.out.println(env.getProperty("spring.application.name"));
        System.out.println(env.getProperty("spring.profiles.active"));
        System.out.println(env.getProperty("user.person-one"));
        System.out.println(env.getProperty("user.person-two"));
        System.out.println(env.getProperty("user.person-three"));
    }

    private static void printPropertySources(ApplicationEnvironment env) {
        for (PropertySource<?> propertySource : env.getPropertySources()) {
            System.out.println(propertySource);
        }
    }

    private static void printPropertySources(ApplicationEnvironment env, String msg) {
        System.out.println(msg);
        printPropertySources(env);
    }

    /**
     * 配置文件后处理器
     */
    @Test
    public void test2() {
        SpringApplication springApplication = new SpringApplication();
        ApplicationEnvironment env = new ApplicationEnvironment();
        ConfigDataEnvironmentPostProcessor configDataEnvironmentPostProcessor = new ConfigDataEnvironmentPostProcessor(new DeferredLogs(), new DefaultBootstrapContext());
        printPropertySources(env, "执行前");
        configDataEnvironmentPostProcessor.postProcessEnvironment(env, springApplication);
        printPropertySources(env, "执行后");
        System.out.println(env.getProperty("spring.application.name"));
    }

    /**
     * Random 后处理器
     */
    @Test
    public void test3() {
        SpringApplication springApplication = new SpringApplication();
        ApplicationEnvironment env = new ApplicationEnvironment();
        RandomValuePropertySourceEnvironmentPostProcessor postProcessor = new RandomValuePropertySourceEnvironmentPostProcessor(new DeferredLogs());
        printPropertySources(env, "执行前");
        postProcessor.postProcessEnvironment(env, springApplication);
        printPropertySources(env, "执行后");
        System.out.println(env.getProperty("random.int"));
        System.out.println(env.getProperty("random.int"));
        System.out.println(env.getProperty("random.int"));
        System.out.println(env.getProperty("random.uuid"));
        System.out.println(env.getProperty("random.uuid"));
        System.out.println(env.getProperty("random.uuid"));
    }

    /**
     * 事件驱动
     */
    @Test
    public void test4() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        SpringApplication springApplication = new SpringApplication();
        // 添加环境后处理器的监听器
        springApplication.addListeners(new EnvironmentPostProcessorApplicationListener());
        ApplicationEnvironment env = new ApplicationEnvironment();
        SpringApplicationRunListener publisher = getPublisher(springApplication);
        // 发布事件
        printPropertySources(env,"事件发布前");
        publisher.environmentPrepared(new DefaultBootstrapContext(), env);
        printPropertySources(env,"事件发布后");

    }

    private static SpringApplicationRunListener getPublisher(SpringApplication springApplication) throws NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        Constructor<?> constructor = Class.forName("org.springframework.boot.context.event.EventPublishingRunListener").getDeclaredConstructor(SpringApplication.class, String[].class);
        constructor.setAccessible(true);
        return (SpringApplicationRunListener)constructor.newInstance(springApplication, new String[0]);
    }

    @Data
    static class User{
        private String firstName;
        private String middleName;
        private String lastName;
    }

    /**
     * Binder
     */
    @Test
    public void test5() throws IOException {
        ApplicationEnvironment env = new ApplicationEnvironment();
        env.getPropertySources().addLast(new ResourcePropertySource("user", new ClassPathResource("user.properties")));
        User user = Binder.get(env).bind("user", User.class).get();
        System.out.println(user);
    }

    /**
     * Binder
     */
    @Test
    public void test6() throws IOException {
        ApplicationEnvironment env = new ApplicationEnvironment();
        env.getPropertySources().addLast(new ResourcePropertySource("user", new ClassPathResource("user.properties")));
        User user = new User();
        Binder.get(env).bind("user", Bindable.ofInstance(user));
        System.out.println(user);
    }

    /**
     * 绑定 Application 属性
     * @throws IOException
     */
    @Test
    public void test7() throws IOException {
        SpringApplication springApplication = new SpringApplication();
        ApplicationEnvironment env = new ApplicationEnvironment();
        env.getPropertySources().addLast(new ResourcePropertySource("app", new ClassPathResource("app.properties")));
        System.out.println(springApplication);
        Binder.get(env).bind("spring.main", Bindable.ofInstance(springApplication));
        System.out.println(springApplication);
    }
}
