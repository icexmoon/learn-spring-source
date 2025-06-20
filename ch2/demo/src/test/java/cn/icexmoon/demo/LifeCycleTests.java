package cn.icexmoon.demo;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * @ClassName LifeCycleTests
 * @Description
 * @Author icexmoon@qq.com
 * @Date 2025/6/20 上午9:56
 * @Version 1.0
 */
@SpringJUnitConfig
public class LifeCycleTests {
    static class MyBeanPostProcessor implements InstantiationAwareBeanPostProcessor, DestructionAwareBeanPostProcessor {

        @Override
        public void postProcessBeforeDestruction(Object bean, String beanName) throws BeansException {
            if ("myBean".equals(beanName))
                System.out.println("postProcessBeforeDestruction() is called.");
        }

        @Override
        public boolean requiresDestruction(Object bean) {
            return DestructionAwareBeanPostProcessor.super.requiresDestruction(bean);
        }

        @Override
        public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
            if ("myBean".equals(beanName))
                System.out.println("postProcessBeforeInstantiation() is called.");
            return InstantiationAwareBeanPostProcessor.super.postProcessBeforeInstantiation(beanClass, beanName);
        }

        @Override
        public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
            if ("myBean".equals(beanName))
                System.out.println("postProcessAfterInstantiation() is called.");
            return InstantiationAwareBeanPostProcessor.super.postProcessAfterInstantiation(bean, beanName);
        }

        @Override
        public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
            if ("myBean".equals(beanName))
                System.out.println("postProcessProperties() is called.");
            return InstantiationAwareBeanPostProcessor.super.postProcessProperties(pvs, bean, beanName);
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            if ("myBean".equals(beanName))
                System.out.println("postProcessBeforeInitialization() is called.");
            return InstantiationAwareBeanPostProcessor.super.postProcessBeforeInitialization(bean, beanName);
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            if ("myBean".equals(beanName))
                System.out.println("postProcessAfterInitialization() is called.");
            return InstantiationAwareBeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
        }
    }

    static class MyBean {
        private ApplicationContext applicationContext;

        public MyBean() {
            System.out.println("MyBean's constructor is called.");
        }

        @Autowired
        public void setApplicationContext(ApplicationContext applicationContext) {
            System.out.println("MyBean's dependency injection method is called.");
            this.applicationContext = applicationContext;
        }

        @PostConstruct
        public void postConstruct() {
            System.out.println("MyBean's postConstruct is called.");
        }

        @PreDestroy
        public void preDestroy() {
            System.out.println("MyBean's preDestroy is called.");
        }
    }

    @Configuration
    static class Config {
        @Bean
        MyBeanPostProcessor myBeanPostProcessor() {
            return new MyBeanPostProcessor();
        }

        @Bean
        MyBean myBean() {
            return new MyBean();
        }
    }

    @Test
    public void testLifeCycle() {

    }
}
