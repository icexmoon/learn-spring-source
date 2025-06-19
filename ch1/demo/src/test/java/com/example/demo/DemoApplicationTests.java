package com.example.demo;

import jakarta.annotation.Resource;
import lombok.Getter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import java.util.Map;
import java.util.Objects;

@SpringBootTest
class DemoApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    public void testBeanFactory() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        this.print(beanFactory);
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Config.class)
                .setScope(BeanDefinition.SCOPE_SINGLETON)
                .getBeanDefinition();
        beanFactory.registerBeanDefinition("config", beanDefinition);
        this.print(beanFactory);
    }

    @Test
    public void testBeanFactory2() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 添加后处理器
        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);
        this.print(beanFactory);
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Config.class)
                .setScope(BeanDefinition.SCOPE_SINGLETON)
                .getBeanDefinition();
        beanFactory.registerBeanDefinition("config", beanDefinition);
        this.print(beanFactory);
    }

    @Test
    public void testBeanFactory3() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 添加后处理器
        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);
        Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessorMap = beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);
        this.print(beanFactory);
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Config.class)
                .setScope(BeanDefinition.SCOPE_SINGLETON)
                .getBeanDefinition();
        beanFactory.registerBeanDefinition("config", beanDefinition);
        beanFactoryPostProcessorMap.forEach((k, v) -> {
            v.postProcessBeanFactory(beanFactory);
        });
        this.print(beanFactory);
    }

    @Test
    public void testBeanFactory4() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 添加后处理器
        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);
        Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessorMap = beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Config.class)
                .setScope(BeanDefinition.SCOPE_SINGLETON)
                .getBeanDefinition();
        beanFactory.registerBeanDefinition("config", beanDefinition);
        beanFactoryPostProcessorMap.forEach((k, v) -> {
            v.postProcessBeanFactory(beanFactory);
        });
        System.out.println(beanFactory.getBean(Bean1.class));
        Bean2 bean2 = beanFactory.getBean(Bean2.class);
        System.out.println(bean2);
        System.out.println(bean2.getBean1());
    }

    @Test
    public void testBeanFactory5() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 添加后处理器
        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);
        this.print(beanFactory);
        Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessorMap = beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Config.class)
                .setScope(BeanDefinition.SCOPE_SINGLETON)
                .getBeanDefinition();
        beanFactory.registerBeanDefinition("config", beanDefinition);
        beanFactoryPostProcessorMap.forEach((k, v) -> {
            v.postProcessBeanFactory(beanFactory);
        });
        // 为工厂添加 Bean 的后处理器
        Map<String, BeanPostProcessor> beanPostProcessorMap = beanFactory.getBeansOfType(BeanPostProcessor.class);
        beanPostProcessorMap.forEach((k, v) -> {
            beanFactory.addBeanPostProcessor(v);
        });
        System.out.println("获取 Bean 实例");
        System.out.println(beanFactory.getBean(Bean1.class));
        Bean2 bean2 = beanFactory.getBean(Bean2.class);
        System.out.println(bean2);
        System.out.println(bean2.getBean1());
    }

    @Test
    public void testBeanFactory6() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 添加后处理器
        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);
        this.print(beanFactory);
        Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessorMap = beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Config2.class)
                .setScope(BeanDefinition.SCOPE_SINGLETON)
                .getBeanDefinition();
        beanFactory.registerBeanDefinition("config2", beanDefinition);
        beanFactoryPostProcessorMap.forEach((k, v) -> {
            v.postProcessBeanFactory(beanFactory);
        });
        // 为工厂添加 Bean 的后处理器
        Map<String, BeanPostProcessor> beanPostProcessorMap = beanFactory.getBeansOfType(BeanPostProcessor.class);
        beanPostProcessorMap.forEach((k, v) -> {
            System.out.println("添加 Bean 后处理器：" + v);
            beanFactory.addBeanPostProcessor(v);
        });
        System.out.println("获取 Bean 实例");
        System.out.println(beanFactory.getBean(Bean5.class).getBean4());
    }

    @Test
    public void testBeanFactory7() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 添加后处理器
        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);
        this.print(beanFactory);
        Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessorMap = beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Config2.class)
                .setScope(BeanDefinition.SCOPE_SINGLETON)
                .getBeanDefinition();
        beanFactory.registerBeanDefinition("config2", beanDefinition);
        beanFactoryPostProcessorMap.forEach((k, v) -> {
            v.postProcessBeanFactory(beanFactory);
        });
        // 为工厂添加 Bean 的后处理器
        Map<String, BeanPostProcessor> beanPostProcessorMap = beanFactory.getBeansOfType(BeanPostProcessor.class);
        beanPostProcessorMap.values().stream()
                .sorted(Objects.requireNonNull(beanFactory.getDependencyComparator()))
                .forEach((v) -> {
            System.out.println("添加 Bean 后处理器：" + v);
            beanFactory.addBeanPostProcessor(v);
            if (v instanceof Ordered){
                Ordered ordered = (Ordered) v;
                int order = ordered.getOrder();
                System.out.println(order);
            }
        });
        System.out.println("获取 Bean 实例");
        System.out.println(beanFactory.getBean(Bean5.class).getBean4());
    }

    @Test
    public void testBeanFactory8() {
        DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();
        // 添加后处理器
        AnnotationConfigUtils.registerAnnotationConfigProcessors(beanFactory);
        this.print(beanFactory);
        Map<String, BeanFactoryPostProcessor> beanFactoryPostProcessorMap = beanFactory.getBeansOfType(BeanFactoryPostProcessor.class);
        AbstractBeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(Config2.class)
                .setScope(BeanDefinition.SCOPE_SINGLETON)
                .getBeanDefinition();
        beanFactory.registerBeanDefinition("config2", beanDefinition);
        beanFactoryPostProcessorMap.forEach((k, v) -> {
            v.postProcessBeanFactory(beanFactory);
        });
        // 为工厂添加 Bean 的后处理器
        Map<String, BeanPostProcessor> beanPostProcessorMap = beanFactory.getBeansOfType(BeanPostProcessor.class);
        beanPostProcessorMap.values().stream()
                .sorted(Objects.requireNonNull(beanFactory.getDependencyComparator()))
                .forEach((v) -> {
                    System.out.println("添加 Bean 后处理器：" + v);
                    beanFactory.addBeanPostProcessor(v);
                    if (v instanceof Ordered){
                        Ordered ordered = (Ordered) v;
                        int order = ordered.getOrder();
                        System.out.println(order);
                    }
                });
        // 工厂准备好后立即初始化单例 bean
        beanFactory.preInstantiateSingletons();
        System.out.println("获取 Bean 实例");
        System.out.println(beanFactory.getBean(Bean5.class).getBean4());
    }

    interface Inter {
    }

    static class Bean3 implements Inter {
    }

    static class Bean4 implements Inter {
    }

    static class Bean5 {
        @Getter
        @Autowired
        @Resource(name = "bean3")
        private Inter bean4;
    }

    static class Config2 {
        @Bean
        public Bean3 bean3() {
            return new Bean3();
        }

        @Bean
        public Bean4 bean4() {
            return new Bean4();
        }

        @Bean
        public Bean5 bean5() {
            return new Bean5();
        }
    }

    static class Bean1 {
    }

    static class Bean2 {
        @Getter
        @Autowired
        private Bean1 bean1;
    }

    @Configuration
    static class Config {
        @Bean
        public Bean1 bean1() {
            return new Bean1();
        }

        @Bean
        public Bean2 bean2() {
            return new Bean2();
        }
    }

    private void print(DefaultListableBeanFactory beanFactory) {
        String[] beanDefinitionNames = beanFactory.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }
    }

}
