package cn.icexmoon.demo1;

import cn.icexmoon.demo1.entity.Teacher;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.boot.*;
import org.springframework.boot.web.reactive.context.AnnotationConfigReactiveWebApplicationContext;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebApplicationContext;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.annotation.*;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.SpringFactoriesLoader;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName ApplicationRunTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/7/7 上午11:02
 * @Version 1.0
 */
@Slf4j
public class ApplicationRunTests {
    /**
     * 事件发布
     *
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws InstantiationException
     * @throws IllegalAccessException
     */
    @Test
    public void testEventPublish() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        SpringApplication springApplication = new SpringApplication();
        // 监听事件
        springApplication.addListeners(event -> System.out.println(event.getClass()));
        // 获取事件发布器
        List<String> factoryNames = SpringFactoriesLoader.loadFactoryNames(SpringApplicationRunListener.class, springApplication.getClassLoader());
        for (String factoryName : factoryNames) {
            log.debug("factoryName: {}", factoryName);
            Constructor<?> constructor = Class.forName(factoryName).getDeclaredConstructor(SpringApplication.class, String[].class);
            constructor.setAccessible(true);
            SpringApplicationRunListener runListener = (SpringApplicationRunListener) constructor.newInstance(springApplication, new String[0]);
            DefaultBootstrapContext bootstrapContext = new DefaultBootstrapContext();
            // 开始执行 run 方法
            runListener.starting(bootstrapContext);
            // 准备环境信息
            runListener.environmentPrepared(bootstrapContext, new StandardEnvironment());
            // 创建容器，调用容器初始化器对容器初始化
            GenericApplicationContext context = new GenericApplicationContext();
            runListener.contextPrepared(context);
            // 加载 bean 定义
            runListener.contextLoaded(context);
            // 刷新容器，创建单例 bean（如果需要）,执行 bean 工厂的后处理器
            context.refresh();
            runListener.started(context, null);
            // 执行 CommandLineRunner 和 ApplicationRunner
            runListener.ready(context, null);

            // RUN 方法执行过程中如果有异常产生
            runListener.failed(context, new RuntimeException("test"));
        }
    }

    /**
     * 创建容器
     */
    @Test
    public void testCreateContext() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method deduceFromClasspath = WebApplicationType.class.getDeclaredMethod("deduceFromClasspath");
        deduceFromClasspath.setAccessible(true);
        WebApplicationType applicationType = (WebApplicationType) deduceFromClasspath.invoke(null);
        GenericApplicationContext context = null;
        switch (applicationType) {
            case NONE -> context = new AnnotationConfigApplicationContext();
            case SERVLET -> context = new AnnotationConfigServletWebApplicationContext();
            case REACTIVE -> context = new AnnotationConfigReactiveWebApplicationContext();
        }
        System.out.println(context.getClass());
    }

    /**
     * 执行容器的初始化器
     */
    @Test
    public void testContextInitializer() {
        SpringApplication springApplication = new SpringApplication();
        // 模拟构造器中添加初始化器
        springApplication.addInitializers(applicationContext -> {
            log.debug("context initializer is called.");
        });
        GenericApplicationContext context = new GenericApplicationContext();
        // 模拟 run 方法中调用初始化器
        for (ApplicationContextInitializer initializer : springApplication.getInitializers()) {
            initializer.initialize(context);
        }
    }


    @Configuration
    static class Config {
        @Bean
        public Teacher teacher() {
            return new Teacher();
        }
    }

    @Test
    public void testBeanDefinitionAdd() {
        AnnotationConfigServletWebApplicationContext context = new AnnotationConfigServletWebApplicationContext();
        // 从配置类添加
        DefaultListableBeanFactory defaultListableBeanFactory = context.getDefaultListableBeanFactory();
        AnnotatedBeanDefinitionReader configReader = new AnnotatedBeanDefinitionReader(defaultListableBeanFactory);
        configReader.register(Config.class);
        // 从 XML 文件添加
        XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(defaultListableBeanFactory);
        xmlReader.loadBeanDefinitions("classpath:applicationContext.xml");
        // 通过包扫描添加
        ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner(defaultListableBeanFactory);
        scanner.scan("cn.icexmoon.demo1.entity");
        context.refresh();
        String[] beanDefinitionNames = context.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            log.debug("beanDefinitionName: {}", beanDefinitionName);
        }
    }

    @Configuration
    static class WebConfig {
        @Bean
        public CommandLineRunner commandLineRunner() {
            return new CommandLineRunner() {
                @Override
                public void run(String... args) throws Exception {
                    System.out.println("CommandLineRunner is called.");
                }
            };
        }

        @Bean
        public ApplicationRunner applicationRunner() {
            return new ApplicationRunner() {
                @Override
                public void run(ApplicationArguments args) throws Exception {
                    System.out.println("ApplicationRunner is called.");
                    Set<String> optionNames = args.getOptionNames();
                    for (String optionName : optionNames) {
                        List<String> optionValues = args.getOptionValues(optionName);
                        System.out.println("optionName: " + optionName + ", optionValues: " + optionValues);
                    }
                    List<String> nonOptionArgs = args.getNonOptionArgs();
                    System.out.println("nonOptionArgs: " + nonOptionArgs);
                }
            };
        }
    }

    /**
     * 执行 CommandLineRunner 和 ApplicationRunner
     *
     * @throws Exception
     */
    @Test
    public void testLoadLineRunner() throws Exception {
        AnnotationConfigServletWebApplicationContext context = new AnnotationConfigServletWebApplicationContext(WebConfig.class);
        String[] args = new String[]{
                "--spring.profiles.active=dev",
                "--spring.main.web-application-type=servlet",
                "test"
        };
        Map<String, CommandLineRunner> commandLineRunnerMap = context.getBeansOfType(CommandLineRunner.class);
        for (CommandLineRunner commandLineRunner : commandLineRunnerMap.values()) {
            commandLineRunner.run(args);
        }
        Map<String, ApplicationRunner> applicationRunnerMap = context.getBeansOfType(ApplicationRunner.class);
        for (ApplicationRunner applicationRunner : applicationRunnerMap.values()) {
            applicationRunner.run(new DefaultApplicationArguments(args));
        }
    }
}
